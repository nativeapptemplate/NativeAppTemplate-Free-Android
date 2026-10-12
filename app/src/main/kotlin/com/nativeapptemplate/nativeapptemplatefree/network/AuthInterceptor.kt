package com.nativeapptemplate.nativeapptemplatefree.network

import com.nativeapptemplate.nativeapptemplatefree.BuildConfig
import com.nativeapptemplate.nativeapptemplatefree.datastore.NativeAppTemplatePreferencesDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

/**
 * Adds the session headers (access-token, client, uid, expiry) to requests for the API host only.
 *
 * As a network interceptor it also runs for every redirect OkHttp follows. OkHttp drops only the
 * standard `Authorization` header when a redirect leaves the host, so without the host check a 3xx to
 * another host would hand it the shopkeeper's session.
 */
@Singleton
class AuthInterceptor internal constructor(
  private val natPreferencesDataSource: NativeAppTemplatePreferencesDataSource,
  private val apiHost: String,
) : Interceptor {
  @Inject
  constructor(natPreferencesDataSource: NativeAppTemplatePreferencesDataSource) :
    this(natPreferencesDataSource, BuildConfig.DOMAIN)

  private suspend fun requestHelper(): RequestHelper {
    val userData = natPreferencesDataSource.userData.first()

    return RequestHelper(
      apiAuthToken = userData.token,
      client = userData.client,
      expiry = userData.expiry,
      uid = userData.uid,
    )
  }

  override fun intercept(chain: Interceptor.Chain): Response {
    if (!chain.request().url.host.equals(apiHost, ignoreCase = true)) {
      return chain.proceed(chain.request())
    }

    return runBlocking {
      try {
        val requestBuilder = chain.request().newBuilder()
        requestHelper().getHeaders().forEach { (key, value) ->
          // header() replaces, so a header already on the request is never duplicated.
          requestBuilder.header(key, value)
        }
        chain.proceed(requestBuilder.build())
      } catch (ce: CancellationException) {
        throw IOException(ce)
      }
    }
  }
}
