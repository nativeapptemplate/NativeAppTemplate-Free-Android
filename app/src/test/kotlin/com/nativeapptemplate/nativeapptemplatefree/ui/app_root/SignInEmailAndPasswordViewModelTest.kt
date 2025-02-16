package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Meta
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestLoginRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * These tests use Robolectric because the subject under test (the ViewModel) uses
 * `String.validateEmail` which has a dependency on `android.util.Patterns.EMAIL_ADDRESS`.
 */
@RunWith(RobolectricTestRunner::class)
class SignInEmailAndPasswordViewModelTest {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val loginRepository = TestLoginRepository()

  private lateinit var viewModel: SignInEmailAndPasswordViewModel

  @Before
  fun setUp() {
    viewModel = SignInEmailAndPasswordViewModel(
      loginRepository = loginRepository,
    )
  }

  @Test
  fun stateIsInitiallyNotLoading() = runTest {
    assertFalse(viewModel.uiState.value.isLoading)
  }

  @Test
  fun shopkeeperAndPermission_whenLoggingIn_isSavedInPreference() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    val email = testInputLoggedInShopkeeper.getEmail()!!

    viewModel.updateEmail(email)
    viewModel.updatePassword(testInputPassword)

    assertEquals(viewModel.uiState.value.email, email)
    assertEquals(viewModel.uiState.value.password, testInputPassword)
    assertFalse(viewModel.hasInvalidData())

    loginRepository.sendLoggedInShopkeeper(testInputLoggedInShopkeeper)
    loginRepository.sendPermissions(testInputPermissions)
    viewModel.login()

    val userData = loginRepository.userData.first()

    assertEquals(userData.androidAppVersion, testInputPermissions.getAndroidAppVersion()!!)
    assertEquals(userData.shouldUpdatePrivacy, testInputPermissions.getShouldUpdatePrivacy()!!)
    assertEquals(userData.shouldUpdateTerms, testInputPermissions.getShouldUpdateTerms()!!)
    assertEquals(userData.shopLimitCount, testInputPermissions.getShopLimitCount()!!)

    val uiStateValue = viewModel.uiState.value
    assertEquals(uiStateValue.loggedInShopkeeper, testInputLoggedInShopkeeper)
    assertFalse(uiStateValue.isLoading)
  }

  @Test
  fun blankEmail_isInvalid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updateEmail("")

    assertTrue(viewModel.hasInvalidData())
  }

  @Test
  fun wrongFormatEmail_isInvalid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updateEmail("wrongFormatEmail@com")

    assertTrue(viewModel.hasInvalidDataEmail())
  }

  @Test
  fun blankPassword_isInvalid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updatePassword("")

    assertTrue(viewModel.hasInvalidDataEmail())
  }

  @Test
  fun passwordWithIncorrectLength_isInvalid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updatePassword("1234567")

    assertTrue(viewModel.hasInvalidDataEmail())
  }
}

private const val LOGGED_IN_SHOPKEEPER_TYPE = "shopkeeper_sign_in"
private const val LOGGED_IN_SHOPKEEPER_ID = "5712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val LOGGED_IN_SHOPKEEPER_ACCOUNT_ID = "2140BC6B-1830-45EE-96A4-B4ED5F53AC11"
private const val LOGGED_IN_SHOPKEEPER_PERSONAL_ACCOUNT_ID = "2140BC6B-1830-45EE-96A4-B4ED5F53AC11"
private const val LOGGED_IN_SHOPKEEPER_ACCOUNT_OWNER_ID = "5712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val LOGGED_IN_SHOPKEEPER_ACCOUNT_NAME = "Account1"
private const val LOGGED_IN_SHOPKEEPER_EMAIL = "john@example.com"
private const val LOGGED_IN_SHOPKEEPER_NAME = "John Smith"
private const val LOGGED_IN_SHOPKEEPER_TIME_ZONE = "Tokyo"
private const val LOGGED_IN_SHOPKEEPER_TOKEN = "john@example.com"
private const val LOGGED_IN_SHOPKEEPER_CLIENT = "Vd6GFW-9DaZrU2pzFd-Asa"
private const val LOGGED_IN_SHOPKEEPER_UID = "john@example.com"
private const val LOGGED_IN_SHOPKEEPER_EXPIRY = "1713165114"

private val testInputLoggedInShopkeeperData =
  Data(
    id = LOGGED_IN_SHOPKEEPER_ID,
    type = LOGGED_IN_SHOPKEEPER_TYPE,
    attributes = Attributes(
      accountId = LOGGED_IN_SHOPKEEPER_ACCOUNT_ID,
      personalAccountId = LOGGED_IN_SHOPKEEPER_PERSONAL_ACCOUNT_ID,
      accountOwnerId = LOGGED_IN_SHOPKEEPER_ACCOUNT_OWNER_ID,
      accountName = LOGGED_IN_SHOPKEEPER_ACCOUNT_NAME,
      email = LOGGED_IN_SHOPKEEPER_EMAIL,
      name = LOGGED_IN_SHOPKEEPER_NAME,
      timeZone = LOGGED_IN_SHOPKEEPER_TIME_ZONE,
      token = LOGGED_IN_SHOPKEEPER_TOKEN,
      client = LOGGED_IN_SHOPKEEPER_CLIENT,
      uid = LOGGED_IN_SHOPKEEPER_UID,
      expiry = LOGGED_IN_SHOPKEEPER_EXPIRY,
    )
  )

private val testInputLoggedInShopkeeper = LoggedInShopkeeper(
  datum = testInputLoggedInShopkeeperData,
)

private const val PERMISSION_TYPE = "permission"
private const val PERMISSION_1_ID = "5712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val PERMISSION_2_ID = "5712F2DF-DFC7-A3AA-66BC-191203654A1B"
private const val PERMISSION_3_ID = "5712F2DF-DFC7-A3AA-66BC-191203654A1C"
private const val PERMISSION_1_NAME = "update shops"
private const val PERMISSION_2_NAME = "update organizations"
private const val PERMISSION_3_NAME = "invitation"
private const val PERMISSION_1_TAG = "update_shops"
private const val PERMISSION_2_TAG = "update_organizations"
private const val PERMISSION_3_TAG = "invitation"
private const val PERMISSION_CREATED_AT = "2024-07-01T15:30:35.000Z"
private const val PERMISSION_UPDATED_AT = "2024-07-01T15:30:35.000Z"
private const val PERMISSION_ANDROID_APP_VERSION = 1
private const val PERMISSION_SHOULD_UPDATE_PRIVACY = false
private const val PERMISSION_SHOULD_UPDATE_TERMS = false
private const val PERMISSION_SHOP_LIMIT_COUNT = 99

private val testInputPermissionsData = listOf(
  Data(
    id = PERMISSION_1_ID,
    type = PERMISSION_TYPE,
    attributes = Attributes(
      name = PERMISSION_1_NAME,
      tag = PERMISSION_1_TAG,
      createdAt = PERMISSION_CREATED_AT,
      updatedAt = PERMISSION_UPDATED_AT
    )
  ),
  Data(
    id = PERMISSION_2_ID,
    type = PERMISSION_TYPE,
    attributes = Attributes(
      name = PERMISSION_2_NAME,
      tag = PERMISSION_2_TAG,
      createdAt = PERMISSION_CREATED_AT,
      updatedAt = PERMISSION_UPDATED_AT
    )
  ),
  Data(
    id = PERMISSION_3_ID,
    type = PERMISSION_TYPE,
    attributes = Attributes(
      name = PERMISSION_3_NAME,
      tag = PERMISSION_3_TAG,
      createdAt = PERMISSION_CREATED_AT,
      updatedAt = PERMISSION_UPDATED_AT
    )
  ),
)

private val testInputPermissions = Permissions(
  datum = testInputPermissionsData,
  meta = Meta(
    androidAppVersion = PERMISSION_ANDROID_APP_VERSION,
    shouldUpdatePrivacy = PERMISSION_SHOULD_UPDATE_PRIVACY,
    shouldUpdateTerms = PERMISSION_SHOULD_UPDATE_TERMS,
    shopLimitCount = PERMISSION_SHOP_LIMIT_COUNT,
  )
)

private const val testInputPassword = "password"
