package com.nativeapptemplate.nativeapptemplatefree.i18n

import android.content.Context
import com.nativeapptemplate.nativeapptemplatefree.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * values-ja must be packaged with the app: a `localeFilters` / `resourceConfigurations` in Gradle
 * without `ja` would strip it, and Japanese devices would silently get English.
 */
@RunWith(RobolectricTestRunner::class)
class JapaneseResourcesPackagedTest {
  private val context: Context get() = RuntimeEnvironment.getApplication()

  @Test
  @Config(qualifiers = "en")
  fun inEnglish_showsTheEnglishStrings() {
    // values/strings.xml: title_shops = "Shops".
    assertEquals("Shops", context.getString(R.string.title_shops))
  }

  @Test
  @Config(qualifiers = "ja")
  fun inJapanese_showsTheJapaneseStrings() {
    // The API's ja.yml: activerecord.models.shop = 店舗; item tag states idled / completed = 未完了 / 完了.
    assertEquals("店舗", context.getString(R.string.title_shops))
    assertEquals("未完了", context.getString(R.string.tag_idled))
    // 8 = NativeAppTemplateConstants.MINIMUM_PASSWORD_LENGTH; Japanese has only the "other" plural.
    assertEquals("8文字以上で入力してください。", context.resources.getQuantityString(R.plurals.characters_minimum, 8, 8))
  }
}
