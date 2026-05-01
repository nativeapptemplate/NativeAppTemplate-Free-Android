package com.nativeapptemplate.nativeapptemplatefree.common.errors

sealed class AppError(
  override val errorCode: String,
  override val errorDescription: String,
) : Exception(errorDescription), CodedError {

  class Unexpected(detail: String? = null) : AppError(
    errorCode = "NATIVEAPPTEMPLATE-1001",
    errorDescription = "Unexpected error" + if (detail != null) ": $detail" else "",
  )
}
