package com.nativeapptemplate.nativeapptemplatefree.common.errors

sealed class AppError(
  override val errorCode: String,
  message: String,
) : Exception(message), CodedError {

  /** The user sees the generic [ErrorMessages.unexpected]; [detail] is only in [message], for the log. */
  class Unexpected(detail: String? = null) : AppError(
    errorCode = "NATIVEAPPTEMPLATE-1001",
    message = "Unexpected error" + if (detail != null) ": $detail" else "",
  ) {
    override val errorDescription: String get() = ErrorMessages.unexpected
  }
}
