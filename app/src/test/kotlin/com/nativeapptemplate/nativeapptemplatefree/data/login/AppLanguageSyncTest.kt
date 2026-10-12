package com.nativeapptemplate.nativeapptemplatefree.data.login

import com.nativeapptemplate.nativeapptemplatefree.common.errors.ApiException
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestAppLanguageProvider
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestLoginRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestSignUpRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.emptyUserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Robolectric so android.util.Log works in the failure path.
@RunWith(RobolectricTestRunner::class)
class AppLanguageSyncTest {
  private val signedIn = emptyUserData.copy(isLoggedIn = true, name = "John", email = "john@example.com")

  @Test
  fun localeToSync_whenTheStoredLocaleDiffers_isTheAppLanguage() {
    assertEquals("ja", localeToSync(signedIn.copy(locale = "en"), appLanguageCode = "ja"))
    assertEquals("en", localeToSync(signedIn.copy(locale = "ja"), appLanguageCode = "en"))
  }

  @Test
  fun localeToSync_whenTheStoredLocaleMatches_isNull() {
    assertNull(localeToSync(signedIn.copy(locale = "ja"), appLanguageCode = "ja"))
    assertNull(localeToSync(signedIn.copy(locale = "en"), appLanguageCode = "en"))
  }

  @Test
  fun localeToSync_whenSignedOut_isNull() {
    // No shopkeeper to update: PATCH /shopkeeper_auth needs a session.
    assertNull(localeToSync(emptyUserData.copy(isLoggedIn = false, locale = "en"), appLanguageCode = "ja"))
  }

  @Test
  fun sync_whenTheLanguagesDiffer_patchesAndStoresTheReturnedLocale() = runTest {
    val loginRepository = TestLoginRepository().apply { sendUserData(signedIn.copy(locale = "en")) }
    val signUpRepository = TestSignUpRepository().apply { sendLoggedInShopkeeper(shopkeeperWithLocale("ja")) }
    val syncer = AppLanguageSyncer(loginRepository, signUpRepository, TestAppLanguageProvider("ja"))

    syncer.sync()

    assertEquals(listOf("ja"), signUpRepository.updatedLocales)
    assertEquals("ja", loginRepository.userData.first().locale)
  }

  @Test
  fun sync_whenTheLanguagesMatch_sendsNothing() = runTest {
    val loginRepository = TestLoginRepository().apply { sendUserData(signedIn.copy(locale = "ja")) }
    val signUpRepository = TestSignUpRepository()
    val syncer = AppLanguageSyncer(loginRepository, signUpRepository, TestAppLanguageProvider("ja"))

    syncer.sync()

    assertEquals(emptyList<String>(), signUpRepository.updatedLocales)
  }

  @Test
  fun sync_whenSignedOut_sendsNothing() = runTest {
    val loginRepository = TestLoginRepository().apply { sendUserData(emptyUserData.copy(locale = "en")) }
    val signUpRepository = TestSignUpRepository()
    val syncer = AppLanguageSyncer(loginRepository, signUpRepository, TestAppLanguageProvider("ja"))

    syncer.sync()

    assertEquals(emptyList<String>(), signUpRepository.updatedLocales)
  }

  @Test
  fun sync_whenTheRequestFails_keepsTheStoredLocaleAndDoesNotThrow() = runTest {
    val loginRepository = TestLoginRepository().apply { sendUserData(signedIn.copy(locale = "en")) }
    val signUpRepository = TestSignUpRepository().apply {
      failUpdateLocale(ApiException.UnprocessableError(rawMessage = "Unable to resolve host"))
    }
    val syncer = AppLanguageSyncer(loginRepository, signUpRepository, TestAppLanguageProvider("ja"))

    syncer.sync()

    assertEquals(listOf("ja"), signUpRepository.updatedLocales)
    // Still different, so the next launch or resume tries again.
    assertEquals("en", loginRepository.userData.first().locale)
  }

  private fun shopkeeperWithLocale(locale: String) = LoggedInShopkeeper(
    datum = Data(
      id = "5712F2DF-DFC7-A3AA-66BC-191203654A1A",
      attributes = Attributes(
        name = "John",
        email = "john@example.com",
        timeZone = "Tokyo",
        locale = locale,
        uid = "john@example.com",
      ),
    ),
  )
}
