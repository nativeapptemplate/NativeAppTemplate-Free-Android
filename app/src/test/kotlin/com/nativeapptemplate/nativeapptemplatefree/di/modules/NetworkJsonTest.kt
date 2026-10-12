package com.nativeapptemplate.nativeapptemplatefree.di.modules

import com.nativeapptemplate.nativeapptemplatefree.model.SignUpForUpdate
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkJsonTest {
  // Request bodies are compact: indentation only added bytes to every request.
  @Test
  fun requestBodies_areEncodedWithoutWhitespace() {
    val body = SignUpForUpdate(name = "John Smith", email = "john@example.com", timeZone = "Tokyo", locale = "en")

    val encoded = networkJson.encodeToString(SignUpForUpdate.serializer(), body)

    // SignUpForUpdate's properties in declaration order, as kotlinx.serialization writes them.
    assertEquals(
      """{"name":"John Smith","email":"john@example.com","time_zone":"Tokyo","locale":"en"}""",
      encoded,
    )
  }
}
