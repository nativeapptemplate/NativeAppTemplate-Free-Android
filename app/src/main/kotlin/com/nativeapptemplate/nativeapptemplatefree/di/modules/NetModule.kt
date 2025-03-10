package com.nativeapptemplate.nativeapptemplatefree.di.modules

import androidx.tracing.trace
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nativeapptemplate.nativeapptemplatefree.BuildConfig
import com.nativeapptemplate.nativeapptemplatefree.NatConstants
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
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
    authInterceptor: AuthInterceptor
  ): OkHttpClient =
    OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .callTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
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
      .baseUrl(NatConstants.baseUrlString())
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
  fun okHttpCallFactory(): Call.Factory = trace("NatOkHttpClient") {
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
