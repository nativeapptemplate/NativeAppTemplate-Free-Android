package com.nativeapptemplate.nativeapptemplatefree.common.errors

import android.content.Context
import androidx.annotation.StringRes
import com.nativeapptemplate.nativeapptemplatefree.R

/**
 * The user-facing messages of [CodedError]s that are not the server's own: string resources in the
 * app language (values/ and values-ja/), worded as on iOS.
 *
 * Most errors are made where there is no Context (the network layer), so the application installs
 * its Context here once, first thing in `Application.onCreate`. A message is looked up when it is
 * read, so it follows the app language. Developer details (raw messages, HTTP statuses) are never
 * part of them: they are logged where the error is made, and kept in the exception's message.
 */
object ErrorMessages {
  @Volatile
  private var context: Context? = null

  fun install(context: Context) {
    this.context = context.applicationContext
  }

  /** No usable answer from the server: no network, a timeout, or an error status without a message. */
  val communicationFailed: String get() = string(R.string.error_communication_failed)

  /** An internal error (a response the app could not process, a bug): nothing the user can act on. */
  val unexpected: String get() = string(R.string.error_unexpected)

  private fun string(@StringRes id: Int): String = checkNotNull(context) {
    "ErrorMessages.install was not called (Application.onCreate)"
  }.getString(id)
}
