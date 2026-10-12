package com.nativeapptemplate.nativeapptemplatefree.utils

import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.LocaleList
import android.provider.Settings
import com.nativeapptemplate.nativeapptemplatefree.model.Locales
import java.util.Locale

/**
 * The language the app's UI is shown in, as one the API supports ([Locales.supported]).
 *
 * This is the source of truth for `shopkeepers.locale`: sign-up sends it, and
 * [com.nativeapptemplate.nativeapptemplatefree.data.login.AppLanguageSyncer] keeps the server in step with it.
 */
object AppLanguage {
  /**
   * The first of [languageTags] (most preferred first) whose language the app is translated into,
   * or [Locales.DEFAULT] (English, the unqualified `values/`) when there is none.
   *
   * Android resolves resources by walking the preferred-locale list, so with no `values-fr/`
   * the list [fr, ja] shows `values-ja/`, and [fr, en, ja] stops at English.
   */
  fun code(languageTags: List<String>): String = languageTags
    .map { Locale.forLanguageTag(it).language }
    .firstOrNull { it in Locales.supported }
    ?: Locales.DEFAULT

  fun currentCode(context: Context): String = code(currentLanguageTags(context))

  /**
   * The system screen where the user changes the app's language.
   *
   * Android 13+: this app's own language list (per-app language, from android:localeConfig).
   * Older versions have no per-app language and the app follows the device language, so open the
   * device's language settings; the app's details screen would offer nothing to change.
   */
  fun settingsIntent(packageName: String, sdkInt: Int = SDK_INT): Intent = if (sdkInt >= 33) { // TIRAMISU
    Intent(Settings.ACTION_APP_LOCALE_SETTINGS, Uri.fromParts("package", packageName, null))
  } else {
    Intent(Settings.ACTION_LOCALE_SETTINGS)
  }

  private fun currentLanguageTags(context: Context): List<String> {
    // Android 13+ lets the user pick a language for this app alone (Settings > Apps > Language,
    // listed through android:localeConfig); it overrides the device languages.
    if (SDK_INT >= 33) { // TIRAMISU
      val appLocales = context.getSystemService(LocaleManager::class.java)?.applicationLocales
      if (appLocales != null && !appLocales.isEmpty) return appLocales.tags()
    }

    return context.resources.configuration.locales.tags()
  }

  private fun LocaleList.tags(): List<String> = (0 until size()).map { get(it).toLanguageTag() }
}
