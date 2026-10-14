package com.nativeapptemplate.nativeapptemplatefree.common.errors

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Robolectric for the string resources of the messages (values/ and values-ja/). */
@RunWith(RobolectricTestRunner::class)
class CodedErrorTest {

  @Test
  fun apiError_hasCorrectErrorCode() {
    val error = ApiException.ApiError(code = 422, apiMessage = "Validation failed")
    assertEquals("NATIVEAPPTEMPLATE-2001", error.errorCode)
  }

  // The user sees the API's error_message as is (the server localizes it); the HTTP status is a
  // developer detail, kept in the exception's message for the log (AGENTS.md, Error Handling).
  @Test
  fun apiError_formattedDescription_isTheServerMessageWithoutTheStatus() {
    val error = ApiException.ApiError(code = 422, apiMessage = "Validation failed")
    assertEquals("[NATIVEAPPTEMPLATE-2001] Validation failed", error.formattedDescription)
    assertEquals("Validation failed [Status: 422]", error.message)
  }

  @Test
  fun unprocessableError_hasCorrectErrorCode() {
    val error = ApiException.UnprocessableError(rawMessage = "timeout")
    assertEquals("NATIVEAPPTEMPLATE-2002", error.errorCode)
  }

  // A response the app could not process is a bug, not something the user can act on: the generic
  // values/strings.xml error_unexpected, "An unexpected error occurred." (the iOS wording). The raw
  // detail stays in the exception's message, for the log.
  @Test
  fun unprocessableError_byDefault_showsTheUnexpectedErrorMessage() {
    val error = ApiException.UnprocessableError(rawMessage = "Incomplete account data")
    assertEquals("[NATIVEAPPTEMPLATE-2002] An unexpected error occurred.", error.formattedDescription)
    assertEquals("Not processable error(Incomplete account data).", error.message)
  }

  // No usable answer from the server: values/strings.xml error_communication_failed.
  @Test
  fun unprocessableError_communicationFailure_showsTheCommunicationMessage() {
    val error = ApiException.UnprocessableError(
      rawMessage = "Unable to resolve host",
      userMessage = ApiException.UserMessage.CommunicationFailure,
    )
    assertEquals(
      "[NATIVEAPPTEMPLATE-2002] Something went wrong while communicating with the server. Please try again later.",
      error.formattedDescription,
    )
  }

  // values-ja/strings.xml error_communication_failed, the wording shared with iOS.
  @Test
  @Config(qualifiers = "ja")
  fun unprocessableError_communicationFailure_isInJapanese() {
    val error = ApiException.UnprocessableError(
      rawMessage = "Unable to resolve host",
      userMessage = ApiException.UserMessage.CommunicationFailure,
    )
    assertEquals(
      "[NATIVEAPPTEMPLATE-2002] サーバーとの通信中に問題が発生しました。しばらくしてからもう一度お試しください。",
      error.formattedDescription,
    )
  }

  // The server's own message (devise_token_auth's), already localized, shown as is.
  @Test
  fun unprocessableError_serverMessage_showsItAsIs() {
    val error = ApiException.UnprocessableError(
      rawMessage = "Invalid login credentials. Please try again.",
      httpStatusCode = 401,
      userMessage = ApiException.UserMessage.Server("Invalid login credentials. Please try again."),
    )
    assertEquals("[NATIVEAPPTEMPLATE-2002] Invalid login credentials. Please try again.", error.formattedDescription)
  }

  @Test
  fun appError_unexpected_hasCorrectCode() {
    val error = AppError.Unexpected()
    assertEquals("NATIVEAPPTEMPLATE-1001", error.errorCode)
    assertEquals("[NATIVEAPPTEMPLATE-1001] An unexpected error occurred.", error.formattedDescription)
  }

  // The detail is for developers: in the exception's message (the log), not on screen.
  @Test
  fun appError_unexpected_withDetail_keepsTheDetailOutOfTheDescription() {
    val error = AppError.Unexpected(detail = "null pointer")
    assertEquals("[NATIVEAPPTEMPLATE-1001] An unexpected error occurred.", error.formattedDescription)
    assertEquals("Unexpected error: null pointer", error.message)
  }

  // values-ja/strings.xml error_unexpected, the wording shared with iOS.
  @Test
  @Config(qualifiers = "ja")
  fun appError_unexpected_isInJapanese() {
    assertEquals("[NATIVEAPPTEMPLATE-1001] 予期しないエラーが発生しました。", AppError.Unexpected().formattedDescription)
  }

  @Test
  fun codedDescription_forCodedError_returnsFormattedDescription() {
    val error: Throwable = ApiException.ApiError(code = 500, apiMessage = "Server error")
    assertEquals("[NATIVEAPPTEMPLATE-2001] Server error", error.codedDescription)
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
