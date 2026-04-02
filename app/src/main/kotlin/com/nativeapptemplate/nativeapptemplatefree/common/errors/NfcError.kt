package com.nativeapptemplate.nativeapptemplatefree.common.errors

sealed class NfcError(
  override val errorCode: String,
  override val errorDescription: String,
) : Exception(errorDescription), CodedError {

  class ScanFailed(detail: String? = null) : NfcError(
    errorCode = "NATA-3001",
    errorDescription = "NFC scan operation failed" + if (detail != null) ": $detail" else "",
  )
}
