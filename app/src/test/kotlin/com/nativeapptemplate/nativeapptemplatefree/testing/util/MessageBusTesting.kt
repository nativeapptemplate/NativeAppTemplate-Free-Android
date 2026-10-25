package com.nativeapptemplate.nativeapptemplatefree.testing.util

import com.nativeapptemplate.nativeapptemplatefree.common.Message
import com.nativeapptemplate.nativeapptemplatefree.common.MessageBus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

/** Takes the messages posted so far off the bus, oldest first. */
suspend fun MessageBus.takePosted(): List<Message> = buildList {
  while (true) add(withTimeoutOrNull(1) { messages.first() } ?: break)
}
