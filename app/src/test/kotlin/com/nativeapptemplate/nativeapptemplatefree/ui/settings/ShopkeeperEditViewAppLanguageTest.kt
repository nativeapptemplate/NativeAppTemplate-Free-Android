package com.nativeapptemplate.nativeapptemplatefree.ui.settings

import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestAppLanguageProvider
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestLoginRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestSignUpRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.emptyUserData
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.testSessionLifecycle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/**
 * Edit Profile shows the app language read-only, with a button to the system screen that changes
 * it, instead of an in-app picker. Run in Japanese, so it also checks the screen's translation.
 */
@RunWith(RobolectricTestRunner::class)
class ShopkeeperEditViewAppLanguageTest {
  @get:Rule
  val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  private fun setContent() {
    val loginRepository = TestLoginRepository().apply {
      sendUserData(emptyUserData.copy(isLoggedIn = true, name = "John", email = "john@example.com", locale = "en"))
    }
    val viewModel = ShopkeeperEditViewModel(loginRepository, TestSignUpRepository(), TestAppLanguageProvider("ja"), testSessionLifecycle(loginRepository))
    composeTestRule.setContent {
      ShopkeeperEditView(viewModel = viewModel, onShowSnackbar = { _, _, _ -> true }, onBackClick = {})
    }
  }

  @Test
  @Config(sdk = [33], qualifiers = "ja")
  fun inJapanese_showsTheAppLanguageSection() {
    setContent()

    // values-ja: app_language, change_app_language_in_settings, app_language_emails_too; the
    // language itself is listed in its own name (language_japanese).
    composeTestRule.onNodeWithText("アプリの言語").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("日本語").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("端末の設定で変更").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("メールもこの言語で届きます。").performScrollTo().assertIsDisplayed()
    // The old picker's label (言語) is gone.
    assertEquals(0, composeTestRule.onAllNodesWithTextCount("言語"))
  }

  @Test
  @Config(sdk = [33], qualifiers = "ja")
  fun changeInSettings_opensThisAppsLanguageScreen() {
    setContent()

    // Through the click action: in the small Robolectric window the Update FAB covers the button.
    composeTestRule.onNodeWithText("端末の設定で変更").performScrollTo().performSemanticsAction(SemanticsActions.OnClick)

    val started = shadowOf(composeTestRule.activity).nextStartedActivity
    // Android 13+ (API 33): Settings.ACTION_APP_LOCALE_SETTINGS for "package:<this app>".
    assertEquals(Settings.ACTION_APP_LOCALE_SETTINGS, started.action)
    assertTrue(started.data.toString().startsWith("package:com.nativeapptemplate.nativeapptemplatefree"))
  }

  private fun androidx.compose.ui.test.junit4.AndroidComposeTestRule<*, *>.onAllNodesWithTextCount(text: String): Int =
    onAllNodes(androidx.compose.ui.test.hasText(text, substring = false)).fetchSemanticsNodes().size
}
