package com.nativeapptemplate.nativeapptemplatefree.network

/**
 * Base exception for API errors thrown by repository implementations.
 */
sealed class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause) {

  /**
   * The API returned a structured error response that was successfully deserialized.
   */
  class ApiError(
    val code: Int,
    val apiMessage: String,
  ) : ApiException("$apiMessage [Status: $code]")

  /**
   * The API returned an error response that could not be deserialized.
   */
  class UnprocessableError(
    val rawMessage: String,
    cause: Throwable? = null,
  ) : ApiException("Not processable error($rawMessage).", cause)
}
