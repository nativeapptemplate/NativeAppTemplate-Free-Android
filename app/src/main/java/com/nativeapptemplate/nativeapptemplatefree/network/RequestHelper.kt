package com.nativeapptemplate.nativeapptemplatefree.network

import androidx.collection.ArrayMap
import com.nativeapptemplate.nativeapptemplatefree.BuildConfig

data class RequestHelper @JvmOverloads constructor(
  private val apiAuthToken: String = "",
  private val client: String = "",
  private val expiry: String = "",
  private val uid: String = ""
) {

  fun getHeaders(): ArrayMap<String, String> {
    val headers = ArrayMap<String, String>()
    headers[SOURCE] = ANDROID
    headers[HEADER_CLIENT_NAME] = BuildConfig.APPLICATION_ID
    headers[HEADER_CLIENT_VERSION] = BuildConfig.VERSION_NAME
    headers[ACCEPT] = "application/vnd.api+json; charset=utf-8"
    headers[CONTENT_TYPE] = "application/json"

    if (apiAuthToken.isNotEmpty()) {
      headers[ACCESS_TOKEN] = apiAuthToken
      headers[TOKEN_TYPE] = BEARER
      headers[CLIENT] = client
      headers[EXPIRY] = expiry
      headers[UID] = uid
    }

    return headers
  }

  companion object {
    private const val CONTENT_TYPE = "Content-Type"
    private const val ACCEPT = "Accept"
    private const val BEARER = "Bearer "
    private const val HEADER_CLIENT_NAME = "client-name"
    private const val HEADER_CLIENT_VERSION = "client-version"
    private const val SOURCE = "source"
    private const val ANDROID = "android"

    private const val ACCESS_TOKEN = "access-token"
    private const val TOKEN_TYPE = "token-type"
    private const val CLIENT = "client"
    private const val EXPIRY = "expiry"
    private const val UID = "uid"
  }
}
