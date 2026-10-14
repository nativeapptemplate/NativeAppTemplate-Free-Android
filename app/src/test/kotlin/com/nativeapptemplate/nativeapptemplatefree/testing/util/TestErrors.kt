package com.nativeapptemplate.nativeapptemplatefree.testing.util

import com.nativeapptemplate.nativeapptemplatefree.common.errors.ApiException

/** A request the API rejected, as `throwApiError` turns a 422 with an error body into an exception. */
val testApiError = ApiException.ApiError(code = 422, apiMessage = "Name has already been taken", httpStatusCode = 422)

/**
 * What a screen must show for [testApiError]: CodedError.codedDescription is "[<code>] <description>"
 * (AGENTS.md, Error Handling). A failed request is NATIVEAPPTEMPLATE-2001 whose description is the API's
 * message as is (the server localizes it); the HTTP status goes to the log, not the screen.
 */
const val TEST_API_ERROR_MESSAGE = "[NATIVEAPPTEMPLATE-2001] Name has already been taken"
