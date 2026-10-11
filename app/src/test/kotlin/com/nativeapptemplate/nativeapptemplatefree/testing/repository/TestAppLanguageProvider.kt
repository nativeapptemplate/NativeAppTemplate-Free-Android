package com.nativeapptemplate.nativeapptemplatefree.testing.repository

import com.nativeapptemplate.nativeapptemplatefree.data.login.AppLanguageProvider

/** An [AppLanguageProvider] whose language the test sets. */
class TestAppLanguageProvider(var code: String = "en") : AppLanguageProvider {
  override fun currentCode(): String = code
}
