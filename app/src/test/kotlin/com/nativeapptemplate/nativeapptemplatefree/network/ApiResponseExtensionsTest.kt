package com.nativeapptemplate.nativeapptemplatefree.network

import com.nativeapptemplate.nativeapptemplatefree.common.errors.ApiException
import com.nativeapptemplate.nativeapptemplatefree.common.errors.codedDescription
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

/** Robolectric for the string resources of the messages and for android.util.Log. */
@RunWith(RobolectricTestRunner::class)
class ApiResponseExtensionsTest {

  @Test
  fun emitApiResponse_onSuccess_emitsData() = runTest {
    val response = ApiResponse.Success(data = "hello")
    val result = flow { emitApiResponse(response) }.first()
    assertEquals("hello", result)
  }

  @Test
  fun emitApiResponse_withTransform_onSuccess_emitsTransformedValue() = runTest {
    val response = ApiResponse.Success(data = "hello")
    val result = flow { emitApiResponse<String, Boolean>(response) { true } }.first()
    assertTrue(result)
  }

  @Test
  fun emitApiResponse_onFailure_throwsUnprocessableError() = runTest {
    val response = ApiResponse.Failure.Exception(Exception("network error"))
    val exception = assertFailsWith<ApiException.UnprocessableError> {
      flow { emitApiResponse<String>(response) }.first()
    }
    assertEquals("network error", exception.rawMessage)
  }

  @Test
  fun emitApiResponse_withTransform_onFailure_throwsUnprocessableError() = runTest {
    val response = ApiResponse.Failure.Exception(Exception("network error"))
    val exception = assertFailsWith<ApiException.UnprocessableError> {
      flow { emitApiResponse<String, Boolean>(response) { true } }.first()
    }
    assertEquals("network error", exception.rawMessage)
  }

  @Test
  fun throwApiError_throwsUnprocessableError_withMessage() {
    val response: ApiResponse<String> = ApiResponse.Failure.Exception(Exception("timeout"))
    val exception = assertFailsWith<ApiException.UnprocessableError> {
      throwApiError(response, "timeout")
    }
    assertEquals("timeout", exception.rawMessage)
    assertEquals("Not processable error(timeout).", exception.message)
  }

  @Test
  fun emitApiResponse_onFailure_exceptionIsApiException() = runTest {
    val response: ApiResponse<String> = ApiResponse.Failure.Exception(Exception("server error"))
    val exception = assertFailsWith<ApiException> {
      flow { emitApiResponse<String>(response) }.first()
    }
    assertIs<ApiException.UnprocessableError>(exception)
  }

  @Test
  fun emitApiResponse_withTransform_onFailure_exceptionIsApiException() = runTest {
    val response: ApiResponse<String> = ApiResponse.Failure.Exception(Exception("server error"))
    val exception = assertFailsWith<ApiException> {
      flow { emitApiResponse<String, Boolean>(response) { true } }.first()
    }
    assertIs<ApiException.UnprocessableError>(exception)
  }

  private fun httpError(status: Int, body: String): ApiResponse<String> = ApiResponse.Failure.Error(
    Response.error<String>(status, body.toResponseBody("application/json".toMediaType())),
  )

  @Test
  fun throwApiError_httpErrorWithUnknownBody_keepsStatusCode() {
    // devise_token_auth answers an invalid session with 401 and {"errors": [...]}, not {code, error_message}.
    val response = httpError(401, """{"errors":["You need to sign in or sign up before continuing."]}""")

    val exception = assertFailsWith<ApiException.UnprocessableError> {
      throwApiError(response, "unauthorized")
    }

    assertEquals(401, exception.httpStatusCode)
    assertTrue(exception.isUnauthorized)
  }

  @Test
  fun throwApiError_httpErrorWithApiErrorBody_keepsStatusCode() {
    val response = httpError(422, """{"code":422,"error_message":"Name can't be blank"}""")

    val exception = assertFailsWith<ApiException.ApiError> {
      throwApiError(response, "unprocessable")
    }

    assertEquals(422, exception.httpStatusCode)
    assertEquals("Name can't be blank", exception.apiMessage)
    assertFalse(exception.isUnauthorized)
  }

  @Test
  fun throwApiError_accountNotFound_isAccountGone() {
    // AccountNotFoundController's answer for an organization that no longer exists
    val response = httpError(
      404,
      """{"code":404,"error_code":"account_not_found","error_message":"This organization no longer exists."}""",
    )

    val exception = assertFailsWith<ApiException.ApiError> {
      throwApiError(response, "not found")
    }

    assertEquals("account_not_found", exception.apiErrorCode)
    assertTrue(exception.isAccountGone)
  }

  @Test
  fun throwApiError_otherNotFound_isNotAccountGone() {
    val response = httpError(404, """{"code":404,"error_message":"Not found."}""")

    val exception = assertFailsWith<ApiException.ApiError> {
      throwApiError(response, "not found")
    }

    assertFalse(exception.isAccountGone)
  }

  @Test
  fun throwApiError_noResponse_hasNoStatusCode() {
    val response: ApiResponse<String> = ApiResponse.Failure.Exception(java.io.IOException("Unable to resolve host"))

    val exception = assertFailsWith<ApiException.UnprocessableError> {
      throwApiError(response, "Unable to resolve host")
    }

    assertNull(exception.httpStatusCode)
    assertFalse(exception.isUnauthorized)
  }

  @Test
  fun emitApiResponse_deviseErrorBody_showsTheServerMessage() = runTest {
    val response = httpError(401, """{"success":false,"errors":["Invalid login credentials. Please try again."]}""")

    val exception = assertFailsWith<ApiException.UnprocessableError> {
      flow { emitApiResponse(response) }.first()
    }

    // Not Sandwich's message(), which is the Retrofit Response's toString(): "Response{protocol=...}".
    assertEquals("Invalid login credentials. Please try again.", exception.rawMessage)
  }

  @Test
  fun emitApiResponse_nonJsonErrorBody_showsTheHttpStatus() = runTest {
    // e.g. an HTML error page from a proxy.
    val response = httpError(502, "<html><body>Bad Gateway</body></html>")

    val exception = assertFailsWith<ApiException.UnprocessableError> {
      flow { emitApiResponse(response) }.first()
    }

    assertEquals("HTTP 502", exception.rawMessage)
  }

  @Test
  fun emitApiResponse_deviseFullMessagesBody_showsTheFirstMessage() = runTest {
    // devise_token_auth's registration errors: {"errors": {"<field>": [...], "full_messages": [...]}}.
    val response = httpError(
      422,
      """{"status":"error","errors":{"email":["has already been taken"],"full_messages":["Email has already been taken","Password is too short"]}}""",
    )

    val exception = assertFailsWith<ApiException.UnprocessableError> {
      flow { emitApiResponse(response) }.first()
    }

    assertEquals("Email has already been taken", exception.rawMessage)
    assertEquals(422, exception.httpStatusCode)
  }

  @Test
  fun emitApiResponse_jsonBodyWithoutErrors_showsTheHttpStatus() = runTest {
    // Valid JSON, but neither {code, error_message} nor devise's "errors".
    val response = httpError(500, """{"status":500,"error":"Internal Server Error"}""")

    val exception = assertFailsWith<ApiException.UnprocessableError> {
      flow { emitApiResponse(response) }.first()
    }

    assertEquals("HTTP 500", exception.rawMessage)
  }

  @Test
  fun emitApiResponse_emptyErrorBody_showsTheHttpStatus() = runTest {
    val response = httpError(503, "")

    val exception = assertFailsWith<ApiException.UnprocessableError> {
      flow { emitApiResponse(response) }.first()
    }

    assertEquals("HTTP 503", exception.rawMessage)
    assertEquals(503, exception.httpStatusCode)
  }

  @Test
  fun throwApiError_apiErrorBody_takesTheCodeFromTheBodyAndTheStatusFromTheResponse() {
    // The two can differ; ApiError.code (logged as "[Status: code]") is the body's.
    val response = httpError(403, """{"code":1001,"error_message":"Shop limit reached"}""")

    val exception = assertFailsWith<ApiException.ApiError> {
      throwApiError(response, "forbidden")
    }

    assertEquals(1001, exception.code)
    assertEquals(403, exception.httpStatusCode)
    // ApiException.ApiError: the user sees "[NATIVEAPPTEMPLATE-2001] $apiMessage"; the status is in
    // the message, for the log.
    assertEquals("[NATIVEAPPTEMPLATE-2001] Shop limit reached", exception.formattedDescription)
    assertEquals("Shop limit reached [Status: 1001]", exception.message)
  }

  // What the user sees (codedDescription). The generic messages are values/strings.xml's
  // error_communication_failed and error_unexpected, the wording shared with iOS.

  private val communicationFailed =
    "[NATIVEAPPTEMPLATE-2002] Something went wrong while communicating with the server. Please try again later."

  @Test
  fun noResponse_showsTheCommunicationMessage() {
    // No network or a timeout: Sandwich wraps OkHttp's IOException.
    val response: ApiResponse<String> = ApiResponse.Failure.Exception(java.net.UnknownHostException("Unable to resolve host"))

    val exception = assertFailsWith<ApiException.UnprocessableError> {
      throwApiError(response, "Unable to resolve host")
    }

    assertEquals(communicationFailed, exception.codedDescription)
    // The detail stays in the exception's message, for the log.
    assertEquals("Not processable error(Unable to resolve host).", exception.message)
  }

  @Test
  fun errorStatusWithoutAMessage_showsTheCommunicationMessage() = runTest {
    val response = httpError(502, "<html><body>Bad Gateway</body></html>")

    val exception = assertFailsWith<ApiException.UnprocessableError> {
      flow { emitApiResponse(response) }.first()
    }

    assertEquals(communicationFailed, exception.codedDescription)
  }

  @Test
  fun responseTheAppCannotProcess_showsTheUnexpectedErrorMessage() {
    // Not an IOException: e.g. a success body the converter could not decode.
    val response: ApiResponse<String> = ApiResponse.Failure.Exception(IllegalArgumentException("Unexpected JSON token"))

    val exception = assertFailsWith<ApiException.UnprocessableError> {
      throwApiError(response, "Unexpected JSON token")
    }

    assertEquals("[NATIVEAPPTEMPLATE-2002] An unexpected error occurred.", exception.codedDescription)
  }

  @Test
  fun deviseErrorBody_showsTheServerMessageAsIs() = runTest {
    val response = httpError(401, """{"success":false,"errors":["Invalid login credentials. Please try again."]}""")

    val exception = assertFailsWith<ApiException.UnprocessableError> {
      flow { emitApiResponse(response) }.first()
    }

    assertEquals("[NATIVEAPPTEMPLATE-2002] Invalid login credentials. Please try again.", exception.codedDescription)
  }

  @Test
  fun apiErrorBody_showsTheServerMessageWithoutTheStatus() {
    val response = httpError(500, """{"code":500,"error_message":"Something broke on our side."}""")

    val exception = assertFailsWith<ApiException.ApiError> {
      throwApiError(response, "server error")
    }

    assertEquals("[NATIVEAPPTEMPLATE-2001] Something broke on our side.", exception.codedDescription)
  }
}
