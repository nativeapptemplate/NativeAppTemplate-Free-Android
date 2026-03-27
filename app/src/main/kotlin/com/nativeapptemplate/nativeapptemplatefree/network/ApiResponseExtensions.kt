package com.nativeapptemplate.nativeapptemplatefree.network

import com.nativeapptemplate.nativeapptemplatefree.model.NativeAppTemplateApiError
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.message
import com.skydoves.sandwich.retrofit.serialization.deserializeErrorBody
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.flow.FlowCollector

/**
 * Handles an [ApiResponse] by emitting the data on success,
 * or throwing an exception with the API error details on failure.
 */
suspend inline fun <reified T : Any> FlowCollector<T>.emitApiResponse(
  response: ApiResponse<T>,
) {
  response.suspendOnSuccess {
    emit(data)
  }.suspendOnFailure {
    throwApiError(response, message())
  }
}

/**
 * Handles an [ApiResponse] by emitting a mapped value on success,
 * or throwing an exception with the API error details on failure.
 *
 * Useful for delete/update operations that return a Boolean or other transformed type.
 */
suspend inline fun <reified T : Any, R> FlowCollector<R>.emitApiResponse(
  response: ApiResponse<T>,
  crossinline transform: (T) -> R,
) {
  response.suspendOnSuccess {
    emit(transform(data))
  }.suspendOnFailure {
    throwApiError(response, message())
  }
}

/**
 * Extracts error details from a failed [ApiResponse] and throws an appropriate exception.
 */
inline fun <reified T : Any> throwApiError(
  response: ApiResponse<T>,
  errorMessage: String,
): Nothing {
  val nativeAppTemplateApiError: NativeAppTemplateApiError? = try {
    response.deserializeErrorBody<T, NativeAppTemplateApiError>()
  } catch (_: Exception) {
    null
  }

  if (nativeAppTemplateApiError != null) {
    throw Exception("${nativeAppTemplateApiError.message} [Status: ${nativeAppTemplateApiError.code}]")
  } else {
    throw Exception("Not processable error($errorMessage).")
  }
}
