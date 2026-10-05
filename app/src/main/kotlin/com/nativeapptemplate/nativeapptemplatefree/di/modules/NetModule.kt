package com.nativeapptemplate.nativeapptemplatefree.di.modules

import androidx.tracing.trace
import com.nativeapptemplate.nativeapptemplatefree.BuildConfig
import com.nativeapptemplate.nativeapptemplatefree.NativeAppTemplateConstants
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagApi
import com.nativeapptemplate.nativeapptemplatefree.data.login.AccountPasswordApi
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginApi
import com.nativeapptemplate.nativeapptemplatefree.data.login.SignUpApi
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopApi
import com.nativeapptemplate.nativeapptemplatefree.network.AuthInterceptor
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.CertificatePinner
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Dagger module for network ops
 */
@Module
@InstallIn(SingletonComponent::class)
class NetModule {

  /**
   * Provide logging interceptor
   */
  @Singleton
  @Provides
  fun provideLoggingInterceptor(): HttpLoggingInterceptor =
    HttpLoggingInterceptor().apply {
      level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.HEADERS
      } else {
        HttpLoggingInterceptor.Level.NONE
      }
    }

  /**
   * Provide OkHttp
   */
  @Singleton
  @Provides
  fun provideOkHttp(
    loggingInterceptor: HttpLoggingInterceptor,
    authInterceptor: AuthInterceptor,
  ): OkHttpClient =
    OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .callTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .apply {
        certificatePinnerFor(BuildConfig.DOMAIN, apiCertificatePins())?.let { certificatePinner(it) }
      }
      .addNetworkInterceptor(authInterceptor)
      .addInterceptor(loggingInterceptor)
      .build()

  private val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
  }

  private val converter = json.asConverterFactory("application/json".toMediaType())

  @Singleton
  @Provides
  fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .baseUrl(NativeAppTemplateConstants.baseUrlString())
      .client(okHttpClient)
      .addConverterFactory(converter)
      .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
      .build()
  }

  @Provides
  fun provideSignUpApi(retrofit: Retrofit): SignUpApi = SignUpApi.create(retrofit)

  @Provides
  fun provideLoginApi(retrofit: Retrofit): LoginApi = LoginApi.create(retrofit)

  @Provides
  fun provideUpdateAccountPasswordApi(retrofit: Retrofit): AccountPasswordApi = AccountPasswordApi.create(retrofit)

  @Provides
  fun provideShopApi(retrofit: Retrofit): ShopApi = ShopApi.create(retrofit)

  @Provides
  fun provideItemTagApi(retrofit: Retrofit): ItemTagApi = ItemTagApi.create(retrofit)

  @Provides
  @Singleton
  fun okHttpCallFactory(): Call.Factory = trace("NativeAppTemplateOkHttpClient") {
    OkHttpClient.Builder()
      .addInterceptor(
        HttpLoggingInterceptor()
          .apply {
            if (BuildConfig.DEBUG) {
              setLevel(HttpLoggingInterceptor.Level.BODY)
            }
          },
      )
      .build()
  }
}

/** Certificate pins for [domain] from a comma-separated "sha256/..." list; null when there are none. */
internal fun certificatePinnerFor(domain: String, pins: String): CertificatePinner? {
  val hashes = pins.split(',').map { it.trim() }.filter { it.isNotEmpty() }
  if (hashes.isEmpty()) return null

  return CertificatePinner.Builder()
    .apply { hashes.forEach { add(domain, it) } }
    .build()
}

private const val HOSTED_API_DOMAIN = "api.nativeapptemplate.com"

// Leaf and intermediate (Google Trust Services WE1) of the former hosted API.
private const val HOSTED_API_CERT_PINS =
  "sha256/7Thx4p19FEZF2WeuXyjc8kr2t1FtT2zA5wWSWoIhh8A=,sha256/kIdp6NNEd8wsugYyyIYFsi1ylMCED3hZbSR8ZFsa/A4="

/** NATIVEAPPTEMPLATE_API_CERT_PINS, or the hosted API's pins when building for that domain. */
private fun apiCertificatePins(): String = BuildConfig.CERT_PINS.ifEmpty {
  if (BuildConfig.DOMAIN == HOSTED_API_DOMAIN) HOSTED_API_CERT_PINS else ""
}
