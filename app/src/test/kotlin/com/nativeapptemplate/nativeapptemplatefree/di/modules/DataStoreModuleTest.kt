package com.nativeapptemplate.nativeapptemplatefree.di.modules

import androidx.datastore.dataStoreFile
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.nativeapptemplate.nativeapptemplatefree.UserPreferences
import com.nativeapptemplate.nativeapptemplatefree.datastore.UserPreferencesSerializer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class DataStoreModuleTest {

  private fun newAead(): Aead {
    AeadConfig.register()
    return KeysetHandle.generateNew(KeyTemplates.get("AES256_GCM"))
      .getPrimitive(RegistryConfiguration.get(), Aead::class.java)
  }

  @Test
  fun unreadablePreferencesFile_isReplacedWithDefaults_insteadOfFailingEveryRead() = runTest {
    val context = RuntimeEnvironment.getApplication()
    // e.g. the Keystore key was invalidated, or the file was half-written. These bytes fail both
    // decryption and the plain-proto fallback: field 1 claims 127 bytes but none follow.
    context.dataStoreFile("user_preferences.pb").apply {
      parentFile!!.mkdirs()
      writeBytes(byteArrayOf(0x0A, 0x7F))
    }

    val dataStore = DataStoreModule.providesUserPreferencesDataStore(
      context = context,
      ioDispatcher = UnconfinedTestDispatcher(testScheduler),
      scope = backgroundScope,
      userPreferencesSerializer = UserPreferencesSerializer(newAead()),
    )

    assertEquals(UserPreferences.getDefaultInstance(), dataStore.data.first())
  }
}
