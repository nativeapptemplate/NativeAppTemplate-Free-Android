package com.nativeapptemplate.nativeapptemplatefree.network

import com.nativeapptemplate.nativeapptemplatefree.common.errors.ApiException
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

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
}
