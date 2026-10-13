package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestAccountPasswordRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestAppLanguageProvider
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestLoginRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestSignUpRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.testSessionLifecycle
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.SignInEmailAndPasswordView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.SignInEmailAndPasswordViewModel
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.SignUpView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.SignUpViewModel
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.PasswordEditView
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.PasswordEditViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * An empty password field names the password it asks for (values/strings.xml): sign-in and sign-up
 * have one password, "Password is required."; Update Password's second field is the new password,
 * "New password is required.".
 */
@RunWith(RobolectricTestRunner::class)
class PasswordRequiredMessagesTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  private val loginRepository = TestLoginRepository()
  private val signUpRepository = TestSignUpRepository()
  private val appLanguageProvider = TestAppLanguageProvider("en")

  @Test
  fun signIn_withNoPassword_asksForThePassword() {
    val viewModel = SignInEmailAndPasswordViewModel(
      loginRepository = loginRepository,
      sessionLifecycle = testSessionLifecycle(loginRepository, signUpRepository, appLanguageProvider),
    )
    composeTestRule.setContent {
      SignInEmailAndPasswordView(
        viewModel = viewModel,
        onShowForgotPasswordClick = {},
        onShowResendConfirmationInstructionsClick = {},
        onShowSnackbar = { _, _, _ -> true },
        onBackClick = {},
      )
    }

    composeTestRule.onNodeWithText(PASSWORD_IS_REQUIRED).assertExists()
    composeTestRule.onNodeWithText(NEW_PASSWORD_IS_REQUIRED).assertDoesNotExist()
  }

  @Test
  fun signUp_withNoPassword_asksForThePassword() {
    val viewModel = SignUpViewModel(signUpRepository = signUpRepository, appLanguageProvider = appLanguageProvider)
    composeTestRule.setContent {
      SignUpView(
        viewModel = viewModel,
        onShowSnackbar = { _, _, _ -> true },
        onBackClick = {},
      )
    }

    composeTestRule.onNodeWithText(PASSWORD_IS_REQUIRED).assertExists()
    composeTestRule.onNodeWithText(NEW_PASSWORD_IS_REQUIRED).assertDoesNotExist()
  }

  @Test
  fun updatePassword_withNoNewPassword_asksForTheNewPassword() {
    val viewModel = PasswordEditViewModel(accountPasswordRepository = TestAccountPasswordRepository())
    composeTestRule.setContent {
      PasswordEditView(
        viewModel = viewModel,
        onShowSnackbar = { _, _, _ -> true },
        onBackClick = {},
      )
    }

    composeTestRule.onNodeWithText(NEW_PASSWORD_IS_REQUIRED).assertExists()
    composeTestRule.onNodeWithText(PASSWORD_IS_REQUIRED).assertDoesNotExist()
  }
}

// values/strings.xml: password_is_required and new_password_is_required.
private const val PASSWORD_IS_REQUIRED = "Password is required."
private const val NEW_PASSWORD_IS_REQUIRED = "New password is required."
