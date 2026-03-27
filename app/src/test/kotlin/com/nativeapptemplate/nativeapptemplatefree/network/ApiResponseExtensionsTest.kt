package com.nativeapptemplate.nativeapptemplatefree.network

import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertFailsWith

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
  fun emitApiResponse_onFailure_throwsException() = runTest {
    val response = ApiResponse.Failure.Exception(Exception("network error"))
    assertFailsWith<Exception> {
      flow { emitApiResponse<String>(response) }.first()
    }
  }

  @Test
  fun emitApiResponse_withTransform_onFailure_throwsException() = runTest {
    val response = ApiResponse.Failure.Exception(Exception("network error"))
    assertFailsWith<Exception> {
      flow { emitApiResponse<String, Boolean>(response) { true } }.first()
    }
  }

  @Test
  fun throwApiError_includesErrorMessageInException() {
    val response: ApiResponse<String> = ApiResponse.Failure.Exception(Exception("timeout"))
    val exception = assertFailsWith<Exception> {
      throwApiError(response, "timeout")
    }
    assertEquals("Not processable error(timeout).", exception.message)
  }

  @Test
  fun emitApiResponse_onFailure_exceptionContainsNotProcessableError() = runTest {
    val response: ApiResponse<String> = ApiResponse.Failure.Exception(Exception("server error"))
    val exception = assertFailsWith<Exception> {
      flow { emitApiResponse<String>(response) }.first()
    }
    assertTrue(exception.message!!.contains("Not processable error"))
  }

  @Test
  fun emitApiResponse_withTransform_onFailure_exceptionContainsNotProcessableError() = runTest {
    val response: ApiResponse<String> = ApiResponse.Failure.Exception(Exception("server error"))
    val exception = assertFailsWith<Exception> {
      flow { emitApiResponse<String, Boolean>(response) { true } }.first()
    }
    assertTrue(exception.message!!.contains("Not processable error"))
  }
}
