package com.nativeapptemplate.nativeapptemplatefree.utils

import android.app.LocaleManager
import android.content.Context
import android.os.LocaleList
import android.provider.Settings
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class AppLanguageTest {
  // The app has values/ (English) and values-ja/ only, and the API supports "en" and "ja"
  // (Locales.supported); anything else is shown in English, the default resources.

  @Test
  fun code_japanese_isJa() {
    assertEquals("ja", AppLanguage.code(listOf("ja")))
    // A region does not matter: values-ja/ serves ja-JP.
    assertEquals("ja", AppLanguage.code(listOf("ja-JP")))
  }

  @Test
  fun code_english_isEn() {
    assertEquals("en", AppLanguage.code(listOf("en-US")))
    assertEquals("en", AppLanguage.code(listOf("en-GB")))
  }

  @Test
  fun code_unsupportedLanguage_isEn() {
    // No values-fr/: the UI falls back to the default (English) resources.
    assertEquals("en", AppLanguage.code(listOf("fr-FR")))
  }

  @Test
  fun code_noLanguage_isEn() {
    assertEquals("en", AppLanguage.code(emptyList()))
    // "und" is Locale.ROOT's tag, which names no language.
    assertEquals("en", AppLanguage.code(listOf("und")))
  }

  @Test
  fun code_usesTheFirstSupportedLanguageInPreferenceOrder() {
    // Android resolves resources by walking the preferred-locale list (API 24+): with no values-fr/,
    // [fr, ja] shows values-ja/, while [fr, en, ja] stops at English.
    assertEquals("ja", AppLanguage.code(listOf("fr-FR", "ja-JP")))
    assertEquals("en", AppLanguage.code(listOf("fr-FR", "en-US", "ja-JP")))
    assertEquals("en", AppLanguage.code(listOf("en-US", "ja-JP")))
  }

  @Test
  @Config(qualifiers = "ja-rJP")
  fun currentCode_followsTheDeviceLanguage() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    // The device is set to Japanese by the qualifiers above.
    assertEquals("ja", AppLanguage.currentCode(context))
  }

  @Test
  @Config(qualifiers = "en-rUS")
  fun currentCode_englishDevice_isEn() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    assertEquals("en", AppLanguage.currentCode(context))
  }

  @Test
  @Config(sdk = [33], qualifiers = "en-rUS")
  fun currentCode_perAppLanguage_winsOverTheDeviceLanguage() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    // Settings > Apps > NativeAppTemplate > Language = 日本語 on an English device (API 33+).
    context.getSystemService(LocaleManager::class.java).applicationLocales = LocaleList.forLanguageTags("ja")
    val original = Locale.getDefault()
    try {
      Locale.setDefault(Locale.US)
      assertEquals("ja", AppLanguage.currentCode(context))
    } finally {
      Locale.setDefault(original)
    }
  }

  @Test
  fun settingsIntent_android13_opensThisAppsLanguageScreen() {
    val intent = AppLanguage.settingsIntent("com.nativeapptemplate.nativeapptemplatefree", sdkInt = 33)

    // Settings.ACTION_APP_LOCALE_SETTINGS takes the app as a "package:" URI.
    assertEquals(Settings.ACTION_APP_LOCALE_SETTINGS, intent.action)
    assertEquals("package:com.nativeapptemplate.nativeapptemplatefree", intent.data.toString())
  }

  @Test
  fun settingsIntent_beforeAndroid13_opensTheDeviceLanguageScreen() {
    // No per-app language before API 33: the app follows the device language.
    val intent = AppLanguage.settingsIntent("com.nativeapptemplate.nativeapptemplatefree", sdkInt = 32)

    assertEquals(Settings.ACTION_LOCALE_SETTINGS, intent.action)
    assertEquals(null, intent.data)
  }
}
