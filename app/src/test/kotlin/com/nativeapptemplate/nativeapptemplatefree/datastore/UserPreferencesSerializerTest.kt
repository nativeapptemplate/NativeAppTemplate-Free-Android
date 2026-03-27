package com.nativeapptemplate.nativeapptemplatefree.datastore

import androidx.datastore.core.CorruptionException
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig
import com.nativeapptemplate.nativeapptemplatefree.userPreferences
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class UserPreferencesSerializerTest {
  private lateinit var aead: Aead
  private lateinit var userPreferencesSerializer: UserPreferencesSerializer

  @Before
  fun setUp() {
    AeadConfig.register()
    val keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("AES256_GCM"))
    aead = keysetHandle.getPrimitive(Aead::class.java)
    userPreferencesSerializer = UserPreferencesSerializer(aead)
  }

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
    userPreferencesSerializer.writeTo(expectedUserPreferences, outputStream)

    val inputStream = ByteArrayInputStream(outputStream.toByteArray())
    val actualUserPreferences = userPreferencesSerializer.readFrom(inputStream)

    kotlin.test.assertEquals(
      expectedUserPreferences,
      actualUserPreferences,
    )
  }

  @Test
  fun readingUnencryptedProto_migratesSuccessfully() = runTest {
    val expectedUserPreferences = userPreferences {
      isLoggedIn = true
    }

    // Write unencrypted proto (legacy format)
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
