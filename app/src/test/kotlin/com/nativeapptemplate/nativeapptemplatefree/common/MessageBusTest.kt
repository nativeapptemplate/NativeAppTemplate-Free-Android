package com.nativeapptemplate.nativeapptemplatefree.common

import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.testing.util.takePosted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/** A screen posts and closes at once; the app's snackbar takes the notice (as iOS's MessageBus). */
class MessageBusTest {
  private val messageBus = MessageBus()

  // Posted while nothing collects (the app's content being recreated): kept until collected.
  @Test
  fun aMessagePostedBeforeAnyoneCollects_isDelivered() = runTest {
    messageBus.post(Message(R.string.message_shop_deleted))

    assertEquals(Message(R.string.message_shop_deleted), messageBus.messages.first())
  }

  @Test
  fun messages_areDeliveredOnce_inTheOrderPosted() = runTest {
    messageBus.post(Message(R.string.message_shop_created))
    messageBus.post(Message(R.string.message_shop_deleted))

    assertEquals(
      listOf(Message(R.string.message_shop_created), Message(R.string.message_shop_deleted)),
      messageBus.messages.take(2).toList(),
    )
    // Taken: a second collector (the recreated app) does not show them again.
    assertEquals(emptyList<Message>(), messageBus.takePosted())
  }
}
