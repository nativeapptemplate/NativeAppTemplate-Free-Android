package com.nativeapptemplate.nativeapptemplatefree.common.errors

sealed class ApiException(message: String, cause: Throwable? = null) :
  Exception(message, cause), CodedError {

  class ApiError(
    val code: Int,
    val apiMessage: String,
  ) : ApiException("$apiMessage [Status: $code]") {
    override val errorCode: String = "NATIVEAPPTEMPLATE-2001"
    override val errorDescription: String = "$apiMessage [Status: $code]"
  }

  class UnprocessableError(
    val rawMessage: String,
    cause: Throwable? = null,
  ) : ApiException("Not processable error($rawMessage).", cause) {
    override val errorCode: String = "NATIVEAPPTEMPLATE-2002"
    override val errorDescription: String = "Processing error: $rawMessage"
  }
}
