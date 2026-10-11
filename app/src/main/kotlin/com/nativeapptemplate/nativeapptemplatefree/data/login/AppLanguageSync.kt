package com.nativeapptemplate.nativeapptemplatefree.data.login

import android.util.Log
import com.nativeapptemplate.nativeapptemplatefree.model.UserData
import com.nativeapptemplate.nativeapptemplatefree.utils.AppLanguage
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

/**
 * The locale to send in `PATCH /shopkeeper_auth` so the server's `shopkeepers.locale` (the language
 * of its messages and emails) matches the app's language, or null when nothing needs sending.
 */
fun localeToSync(userData: UserData, appLanguageCode: String): String? = when {
  !userData.isLoggedIn -> null
  userData.locale == appLanguageCode -> null
  else -> appLanguageCode
}

/**
 * Makes the signed-in shopkeeper's `locale` follow the app language ([AppLanguage]), which the user
 * changes in the system settings. Runs on every resume and after sign-in. A failure is only logged:
 * the locales still differ, so the next run tries again.
 */
class AppLanguageSyncer @Inject constructor(
  private val loginRepository: LoginRepository,
  private val signUpRepository: SignUpRepository,
  private val appLanguageProvider: AppLanguageProvider,
) {
  suspend fun sync() {
    val locale = localeToSync(loginRepository.userData.first(), appLanguageProvider.currentCode()) ?: return

    val loggedInShopkeeper = signUpRepository.updateLocale(locale)
      .catch { exception ->
        Log.w("AppLanguageSyncer", "Failed to update the locale to $locale", exception)
      }
      .firstOrNull() ?: return

    loginRepository.setShopkeeperForUpdate(loggedInShopkeeper)
  }
}
