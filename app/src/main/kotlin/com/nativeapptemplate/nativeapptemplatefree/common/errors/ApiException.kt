package com.nativeapptemplate.nativeapptemplatefree.common.errors

/**
 * @property httpStatusCode The HTTP status of the failed response, or null when the request
 * never got a response (e.g. no network) or the status is unknown.
 */
sealed class ApiException(
  message: String,
  cause: Throwable? = null,
  val httpStatusCode: Int? = null,
) : Exception(message, cause),
  CodedError {

  /** True when the server rejected the session's credentials. */
  val isUnauthorized: Boolean get() = httpStatusCode == 401

  class ApiError(
    val code: Int,
    val apiMessage: String,
    httpStatusCode: Int? = null,
  ) : ApiException("$apiMessage [Status: $code]", httpStatusCode = httpStatusCode) {
    override val errorCode: String = "NATIVEAPPTEMPLATE-2001"
    override val errorDescription: String = "$apiMessage [Status: $code]"
  }

  class UnprocessableError(
    val rawMessage: String,
    cause: Throwable? = null,
    httpStatusCode: Int? = null,
  ) : ApiException("Not processable error($rawMessage).", cause, httpStatusCode) {
    override val errorCode: String = "NATIVEAPPTEMPLATE-2002"
    override val errorDescription: String = "Processing error: $rawMessage"
  }
}
