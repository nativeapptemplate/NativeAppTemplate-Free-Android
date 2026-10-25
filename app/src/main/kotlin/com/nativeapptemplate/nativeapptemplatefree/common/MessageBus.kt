package com.nativeapptemplate.nativeapptemplatefree.common

import androidx.annotation.StringRes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/** A notice for the app's snackbar */
data class Message(@param:StringRes val textId: Int)

/**
 * Carries notices from the screens to the app's snackbar, as iOS's MessageBus. A screen's own
 * snackbar belongs to its coroutine and goes with it, so a screen that closes once done (created,
 * deleted) posts here and closes at once; NativeAppTemplateApp shows the notice.
 *
 * Kept until taken, so a notice posted while nothing collects (the app being recreated) is shown
 * once something does, and taken once.
 */
@Singleton
class MessageBus @Inject constructor() {
  private val channel = Channel<Message>(Channel.UNLIMITED)

  val messages: Flow<Message> = channel.receiveAsFlow()

  fun post(message: Message) {
    channel.trySend(message)
  }
}
