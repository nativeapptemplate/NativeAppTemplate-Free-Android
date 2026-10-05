package com.nativeapptemplate.nativeapptemplatefree.network

import com.nativeapptemplate.nativeapptemplatefree.common.errors.ApiException
import com.nativeapptemplate.nativeapptemplatefree.model.NativeAppTemplateApiError
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.message
import com.skydoves.sandwich.retrofit.statusCode
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import retrofit2.Response
import java.io.IOException

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
 *
 * [errorMessage] is used only when there was no HTTP response (e.g. no network). For an HTTP error,
 * Sandwich's message() is the Retrofit Response's toString() ("Response{protocol=...}"), so the
 * message comes from the error body instead.
 */
inline fun <reified T : Any> throwApiError(
  response: ApiResponse<T>,
  errorMessage: String,
): Nothing = throwApiErrorFor(response, errorMessage)

private val errorBodyJson = Json { ignoreUnknownKeys = true }

@PublishedApi
internal fun throwApiErrorFor(
  response: ApiResponse<*>,
  errorMessage: String,
): Nothing {
  val httpError = response as? ApiResponse.Failure.Error
    ?: throw ApiException.UnprocessableError(rawMessage = errorMessage)

  val httpStatusCode = httpError.statusCode.code
  // The body can be read only once.
  val errorBody: String? = try {
    (httpError.payload as? Response<*>)?.errorBody()?.string()
  } catch (_: IOException) {
    null
  }

  val nativeAppTemplateApiError: NativeAppTemplateApiError? = errorBody?.let {
    try {
      errorBodyJson.decodeFromString<NativeAppTemplateApiError>(it)
    } catch (_: SerializationException) {
      null
    } catch (_: IllegalArgumentException) {
      null
    }
  }
  if (nativeAppTemplateApiError != null) {
    throw ApiException.ApiError(
      code = nativeAppTemplateApiError.code,
      apiMessage = nativeAppTemplateApiError.message,
      httpStatusCode = httpStatusCode,
    )
  }

  throw ApiException.UnprocessableError(
    rawMessage = errorBody?.let(::deviseErrorMessage) ?: "HTTP $httpStatusCode",
    httpStatusCode = httpStatusCode,
  )
}

/**
 * The first message of a devise_token_auth error body: {"errors": ["..."]} or
 * {"errors": {"full_messages": ["..."]}}. Null for any other body (e.g. an HTML error page).
 */
private fun deviseErrorMessage(errorBody: String): String? {
  val errors = try {
    errorBodyJson.parseToJsonElement(errorBody).jsonObject["errors"]
  } catch (_: SerializationException) {
    return null
  } catch (_: IllegalArgumentException) {
    return null
  }
  val messages = when (errors) {
    is JsonArray -> errors
    is JsonObject -> errors["full_messages"] as? JsonArray
    else -> null
  }
  return (messages?.firstOrNull() as? JsonPrimitive)?.contentOrNull
}
