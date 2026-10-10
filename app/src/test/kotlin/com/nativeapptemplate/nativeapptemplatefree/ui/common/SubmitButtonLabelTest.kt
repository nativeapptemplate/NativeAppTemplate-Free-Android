package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestAccountPasswordRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestLoginRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestShopRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestSignUpRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.emptyUserData
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.ForgotPasswordView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.ForgotPasswordViewModel
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.ResendConfirmationInstructionsView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.ResendConfirmationInstructionsViewModel
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.SignUpView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.SignUpViewModel
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.PasswordEditView
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.PasswordEditViewModel
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.ShopkeeperEditView
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.ShopkeeperEditViewModel
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.ShopBasicSettingsView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.ShopBasicSettingsViewModel
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail.ItemTagEditView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail.ItemTagEditViewModel
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagEditRoute
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ShopBasicSettingsRoute
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Every form's submit button must announce what it does: by its text, or by its icon's label when
 * it shows only an icon. Otherwise TalkBack announces just "Button". Each expected label is the
 * string the screen already uses for that action (e.g. "Update").
 */
@RunWith(RobolectricTestRunner::class)
class SubmitButtonLabelTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  private val onShowSnackbar: suspend (String, String?, androidx.compose.material3.SnackbarDuration?) -> Boolean = { _, _, _ -> true }

  private fun string(id: Int) = RuntimeEnvironment.getApplication().getString(id)

  private fun assertSubmitButtonLabeled(label: Int, content: @Composable () -> Unit) {
    composeTestRule.setContent(content)
    composeTestRule.waitForIdle()
    val announced = hasText(string(label)) or hasContentDescription(string(label))
    composeTestRule.onNode(hasClickAction() and announced).assertIsDisplayed()
  }

  @Test
  fun shopBasicSettings() {
    val shopRepository = TestShopRepository().apply { sendShop(testShop) }
    val viewModel = ShopBasicSettingsViewModel(SavedStateHandle(route = ShopBasicSettingsRoute(id = ID)), shopRepository)
    assertSubmitButtonLabeled(R.string.button_label_update) {
      ShopBasicSettingsView(viewModel = viewModel, onShowSnackbar = onShowSnackbar, onBackClick = {})
    }
  }

  @Test
  fun itemTagEdit() {
    val itemTagRepository = TestItemTagRepository().apply { sendItemTag(testItemTag) }
    val viewModel = ItemTagEditViewModel(SavedStateHandle(route = ItemTagEditRoute(id = ID)), itemTagRepository)
    assertSubmitButtonLabeled(R.string.button_label_update) {
      ItemTagEditView(viewModel = viewModel, onShowSnackbar = onShowSnackbar, onBackClick = {})
    }
  }

  @Test
  fun shopkeeperEdit() {
    val loginRepository = TestLoginRepository().apply { sendUserData(emptyUserData.copy(name = "John", email = "john@example.com")) }
    val viewModel = ShopkeeperEditViewModel(loginRepository, TestSignUpRepository())
    assertSubmitButtonLabeled(R.string.button_label_update) {
      ShopkeeperEditView(viewModel = viewModel, onShowSnackbar = onShowSnackbar, onBackClick = {})
    }
  }

  @Test
  fun passwordEdit() {
    val viewModel = PasswordEditViewModel(TestAccountPasswordRepository())
    assertSubmitButtonLabeled(R.string.button_label_update) {
      PasswordEditView(viewModel = viewModel, onShowSnackbar = onShowSnackbar, onBackClick = {})
    }
  }

  @Test
  fun signUp() {
    val viewModel = SignUpViewModel(TestSignUpRepository())
    assertSubmitButtonLabeled(R.string.sign_up) {
      SignUpView(viewModel = viewModel, onShowSnackbar = onShowSnackbar, onBackClick = {})
    }
  }

  @Test
  fun forgotPassword() {
    val viewModel = ForgotPasswordViewModel(TestSignUpRepository())
    assertSubmitButtonLabeled(R.string.button_send_me_reset_password_instructions) {
      ForgotPasswordView(viewModel = viewModel, onShowSnackbar = onShowSnackbar, onBackClick = {})
    }
  }

  @Test
  fun resendConfirmationInstructions() {
    val viewModel = ResendConfirmationInstructionsViewModel(TestSignUpRepository())
    assertSubmitButtonLabeled(R.string.button_send_me_confirmation_instructions) {
      ResendConfirmationInstructionsView(viewModel = viewModel, onShowSnackbar = onShowSnackbar, onBackClick = {})
    }
  }
}

private const val ID = "5712F2DF-DFC7-A3AA-66BC-191203654A1A"

private val testShop = Shop(datum = Data(id = ID, type = "shop", attributes = Attributes(name = "Shop", description = "", timeZone = "Tokyo")))
private val testItemTag = ItemTag(datum = Data(id = ID, type = "item_tag", attributes = Attributes(name = "A001", description = "", state = "idled")))
