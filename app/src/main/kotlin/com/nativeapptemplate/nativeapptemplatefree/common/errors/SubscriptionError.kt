package com.nativeapptemplate.nativeapptemplatefree.common.errors

sealed class SubscriptionError(
  override val errorCode: String,
  override val errorDescription: String,
) : Exception(errorDescription), CodedError {

  class RestoreFailed(detail: String? = null) : SubscriptionError(
    errorCode = "NATA-6001",
    errorDescription = "Failed to restore purchases" + if (detail != null) ": $detail" else "",
  )

  class SubscriptionRequired : SubscriptionError(
    errorCode = "NATA-6002",
    errorDescription = "User needs an active subscription",
  )
}
