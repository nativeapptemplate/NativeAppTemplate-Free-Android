package com.nativeapptemplate.nativeapptemplatefree.common.errors

import org.junit.Assert.assertEquals
import org.junit.Test

class CodedErrorTest {

  @Test
  fun apiError_hasCorrectErrorCode() {
    val error = ApiException.ApiError(code = 422, apiMessage = "Validation failed")
    assertEquals("NATA-2001", error.errorCode)
  }

  @Test
  fun apiError_formattedDescription_includesCodeAndMessage() {
    val error = ApiException.ApiError(code = 422, apiMessage = "Validation failed")
    assertEquals("[NATA-2001] Validation failed [Status: 422]", error.formattedDescription)
  }

  @Test
  fun unprocessableError_hasCorrectErrorCode() {
    val error = ApiException.UnprocessableError(rawMessage = "timeout")
    assertEquals("NATA-2002", error.errorCode)
  }

  @Test
  fun unprocessableError_formattedDescription_includesCodeAndMessage() {
    val error = ApiException.UnprocessableError(rawMessage = "timeout")
    assertEquals("[NATA-2002] Processing error: timeout", error.formattedDescription)
  }

  @Test
  fun appError_unexpected_hasCorrectCode() {
    val error = AppError.Unexpected()
    assertEquals("NATA-1001", error.errorCode)
    assertEquals("[NATA-1001] Unexpected error", error.formattedDescription)
  }

  @Test
  fun appError_unexpected_withDetail_includesDetail() {
    val error = AppError.Unexpected(detail = "null pointer")
    assertEquals("[NATA-1001] Unexpected error: null pointer", error.formattedDescription)
  }

  @Test
  fun nfcError_scanFailed_hasCorrectCode() {
    val error = NfcError.ScanFailed()
    assertEquals("NATA-3001", error.errorCode)
    assertEquals("[NATA-3001] NFC scan operation failed", error.formattedDescription)
  }

  @Test
  fun nfcError_scanFailed_withDetail_includesDetail() {
    val error = NfcError.ScanFailed(detail = "tag lost")
    assertEquals("[NATA-3001] NFC scan operation failed: tag lost", error.formattedDescription)
  }

  @Test
  fun subscriptionError_restoreFailed_hasCorrectCode() {
    val error = SubscriptionError.RestoreFailed()
    assertEquals("NATA-6001", error.errorCode)
    assertEquals("[NATA-6001] Failed to restore purchases", error.formattedDescription)
  }

  @Test
  fun subscriptionError_subscriptionRequired_hasCorrectCode() {
    val error = SubscriptionError.SubscriptionRequired()
    assertEquals("NATA-6002", error.errorCode)
    assertEquals("[NATA-6002] User needs an active subscription", error.formattedDescription)
  }

  @Test
  fun codedDescription_forCodedError_returnsFormattedDescription() {
    val error: Throwable = ApiException.ApiError(code = 500, apiMessage = "Server error")
    assertEquals("[NATA-2001] Server error [Status: 500]", error.codedDescription)
  }

  @Test
  fun codedDescription_forRegularException_returnsMessage() {
    val error: Throwable = RuntimeException("something broke")
    assertEquals("something broke", error.codedDescription)
  }

  @Test
  fun codedDescription_forExceptionWithNullMessage_returnsUnknownError() {
    val error: Throwable = RuntimeException()
    assertEquals("Unknown Error", error.codedDescription)
  }
}
