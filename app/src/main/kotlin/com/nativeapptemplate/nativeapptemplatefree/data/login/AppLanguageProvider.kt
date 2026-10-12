package com.nativeapptemplate.nativeapptemplatefree.data.login

import android.content.Context
import com.nativeapptemplate.nativeapptemplatefree.utils.AppLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/** The app's current language code ("en" or "ja"); see [AppLanguage.currentCode]. */
interface AppLanguageProvider {
  fun currentCode(): String
}

class AppLanguageProviderImpl @Inject constructor(
  @param:ApplicationContext private val context: Context,
) : AppLanguageProvider {
  override fun currentCode(): String = AppLanguage.currentCode(context)
}
