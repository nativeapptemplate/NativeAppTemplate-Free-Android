package com.nativeapptemplate.nativeapptemplatefree.datastore

import androidx.datastore.core.CorruptionException
import com.nativeapptemplate.nativeapptemplatefree.userPreferences
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class UserPreferencesSerializerTest {
  private val userPreferencesSerializer = UserPreferencesSerializer()

  @Test
  fun defaultUserPreferences_isEmpty() {
    kotlin.test.assertEquals(
      userPreferences {
        // Default value
      },
      userPreferencesSerializer.defaultValue,
    )
  }

  @Test
  fun writingAndReadingUserPreferences_outputsCorrectValue() = runTest {
    val expectedUserPreferences = userPreferences {
      isLoggedIn = true
    }

    val outputStream = ByteArrayOutputStream()

    expectedUserPreferences.writeTo(outputStream)

    val inputStream = ByteArrayInputStream(outputStream.toByteArray())

    val actualUserPreferences = userPreferencesSerializer.readFrom(inputStream)

    kotlin.test.assertEquals(
      expectedUserPreferences,
      actualUserPreferences,
    )
  }

  @Test(expected = CorruptionException::class)
  fun readingInvalidUserPreferences_throwsCorruptionException() = runTest {
    userPreferencesSerializer.readFrom(ByteArrayInputStream(byteArrayOf(0)))
  }
}