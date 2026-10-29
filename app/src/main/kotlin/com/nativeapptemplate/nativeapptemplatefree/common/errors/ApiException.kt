package com.nativeapptemplate.nativeapptemplatefree.common.errors

import android.util.Log

/**
 * @property httpStatusCode The HTTP status of the failed response, or null when the request
 * never got a response (e.g. no network) or the status is unknown.
 */
sealed class ApiException(
  message: String,
  cause: Throwable? = null,
  val httpStatusCode: Int? = null,
) : Exception(message, cause),
  CodedError {

  /** True when the server rejected the session's credentials. */
  val isUnauthorized: Boolean get() = httpStatusCode == 401

  /**
   * True when the API answered account_not_found. It looks up the organization in the path before
   * the token, and a free account has only its personal organization, which goes with the account
   * (deleted by the user, or by the public demo after 30 unused days): the session is no good either.
   */
  val isAccountGone: Boolean
    get() = this is ApiError && httpStatusCode == 404 && apiErrorCode == ACCOUNT_NOT_FOUND

  private companion object {
    // The API's AccountNotFoundController
    const val ACCOUNT_NOT_FOUND = "account_not_found"
  }

  /** @property apiErrorCode The body's `error_code`, which only some errors carry. */
  class ApiError(
    val code: Int,
    val apiMessage: String,
    httpStatusCode: Int? = null,
    val apiErrorCode: String? = null,
  ) : ApiException("$apiMessage [Status: $code]", httpStatusCode = httpStatusCode) {
    override val errorCode: String = "NATIVEAPPTEMPLATE-2001"

    /** The API's message as is (the server localizes it); the status is only in [message], for the log. */
    override val errorDescription: String = apiMessage
  }

  /** What the user sees for an [UnprocessableError]. */
  sealed interface UserMessage {
    /** The server's own message (devise_token_auth's), already localized: shown as is. */
    data class Server(val text: String) : UserMessage

    /** No usable answer: no network, a timeout, or an error status without a message. */
    data object CommunicationFailure : UserMessage

    /** A response the app could not process: a bug, nothing the user can act on. */
    data object Unexpected : UserMessage
  }

  /**
   * No response, an error response that could not be deserialized, or a response the app could not
   * process. [rawMessage] is the developer detail, in [message] for the log; the user sees [userMessage].
   */
  class UnprocessableError(
    val rawMessage: String,
    cause: Throwable? = null,
    httpStatusCode: Int? = null,
    val userMessage: UserMessage = UserMessage.Unexpected,
  ) : ApiException("Not processable error($rawMessage).", cause, httpStatusCode) {
    override val errorCode: String = "NATIVEAPPTEMPLATE-2002"
    override val errorDescription: String
      get() = when (userMessage) {
        is UserMessage.Server -> userMessage.text
        UserMessage.CommunicationFailure -> ErrorMessages.communicationFailed
        UserMessage.Unexpected -> ErrorMessages.unexpected
      }
  }
}

/**
 * Logs this error's code and developer detail (the raw message, the HTTP status), which the user
 * never sees (AGENTS.md, Error Handling). Call it where the error is made. Returns this error.
 */
fun <E : ApiException> E.logged(): E {
  Log.w(API_ERROR_LOG_TAG, "[$errorCode] $message (HTTP ${httpStatusCode ?: "no response"})", cause)
  return this
}

private const val API_ERROR_LOG_TAG = "ApiError"
