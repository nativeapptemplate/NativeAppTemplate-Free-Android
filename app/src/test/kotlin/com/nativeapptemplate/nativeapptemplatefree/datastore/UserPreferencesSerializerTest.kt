package com.nativeapptemplate.nativeapptemplatefree.datastore

import androidx.datastore.core.CorruptionException
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.RegistryConfiguration
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
    aead = keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead::class.java)
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

  // Security: a file the key does not accept is never read as plain proto. Otherwise a plain
  // UserPreferences written to the device (or ciphertext that happens to parse once the Keystore
  // key is gone) would sign the app in as whoever it names. Corruption instead: DataStoreModule's
  // ReplaceFileCorruptionHandler then starts over signed out.
  @Test
  fun readingUnencryptedProto_isCorruption_notASession() = runTest {
    val plantedSession = userPreferences {
      isLoggedIn = true
      id = "shopkeeper-1"
      token = "stolen-token"
    }
    val plainBytes = ByteArrayOutputStream().also { plantedSession.writeTo(it) }.toByteArray()
    // The planted bytes do parse as the session on their own; only the serializer must refuse them.
    kotlin.test.assertEquals(plantedSession, com.nativeapptemplate.nativeapptemplatefree.UserPreferences.parseFrom(plainBytes))

    val exception = kotlin.test.assertFailsWith<CorruptionException> {
      userPreferencesSerializer.readFrom(ByteArrayInputStream(plainBytes))
    }
    kotlin.test.assertIs<java.security.GeneralSecurityException>(exception.cause)
  }

  // The same bytes under another key (e.g. the Keystore key was invalidated and a new one made).
  @Test
  fun readingDataEncryptedWithAnotherKey_isCorruption() = runTest {
    val otherAead = KeysetHandle.generateNew(KeyTemplates.get("AES256_GCM")).getPrimitive(RegistryConfiguration.get(), Aead::class.java)
    val outputStream = ByteArrayOutputStream()
    UserPreferencesSerializer(otherAead).writeTo(userPreferences { isLoggedIn = true }, outputStream)

    kotlin.test.assertFailsWith<CorruptionException> {
      userPreferencesSerializer.readFrom(ByteArrayInputStream(outputStream.toByteArray()))
    }
  }

  @Test(expected = CorruptionException::class)
  fun readingInvalidUserPreferences_throwsCorruptionException() = runTest {
    userPreferencesSerializer.readFrom(ByteArrayInputStream(byteArrayOf(0)))
  }
}
