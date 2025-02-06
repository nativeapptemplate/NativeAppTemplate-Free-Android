package com.nativeapptemplate.nativeapptemplatefree.network

import com.nativeapptemplate.nativeapptemplatefree.datastore.NatPreferencesDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class AuthInterceptor @Inject constructor(
  private val natPreferencesDataSource: NatPreferencesDataSource,
) : Interceptor {
  private suspend fun requestHelper(
  ): RequestHelper {
    val userData = natPreferencesDataSource.userData.first()

    return RequestHelper(
      apiAuthToken = userData.token,
      client = userData.client,
      expiry = userData.expiry,
      uid = userData.uid
    )
  }

  override fun intercept(chain: Interceptor.Chain): Response {
    return runBlocking {
      try {
        val requestBuilder = chain.request().newBuilder()
        requestHelper().getHeaders().forEach { (key, value) ->
          requestBuilder.addHeader(key, value)
        }
        chain.proceed(requestBuilder.build())
      } catch (ce: CancellationException) {
        throw IOException(ce)
      }
    }
  }
}
