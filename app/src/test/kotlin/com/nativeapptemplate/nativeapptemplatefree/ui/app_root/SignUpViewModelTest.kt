package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestSignUpRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.collect
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
class SignUpViewModelTest {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val signUpRepository = TestSignUpRepository()

  private lateinit var viewModel: SignUpViewModel

  @Before
  fun setUp() {
    viewModel = SignUpViewModel(
      signUpRepository = signUpRepository,
    )
  }

  @Test
  fun stateIsInitiallyNotLoading() = runTest {
    assertFalse(viewModel.uiState.value.isLoading)
  }

  @Test
  fun stateIsCreated_whenSigningUp_becomesTrue() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    val name = testInputLoggedInShopkeeper.getName()!!
    val email = testInputLoggedInShopkeeper.getEmail()!!
    val timeZone = testInputLoggedInShopkeeper.getTimeZone()!!

    viewModel.updateName(name)
    viewModel.updateEmail(email)
    viewModel.updatePassword(testInputPassword)
    viewModel.updateTimeZone(timeZone)

    assertEquals(viewModel.uiState.value.name, name)
    assertEquals(viewModel.uiState.value.email, email)
    assertEquals(viewModel.uiState.value.password, testInputPassword)
    assertEquals(viewModel.uiState.value.timeZone, timeZone)
    assertFalse(viewModel.hasInvalidData())

    signUpRepository.sendLoggedInShopkeeper(testInputLoggedInShopkeeper)
    viewModel.createShopkeeper()

    val uiStateValue = viewModel.uiState.value
    assertTrue(uiStateValue.isCreated)
  }

  @Test
  fun blankName_isInvalid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updateName("")

    assertTrue(viewModel.hasInvalidData())
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

    assertTrue(viewModel.hasInvalidData())
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
private const val LOGGED_IN_SHOPKEEPER_TOKEN = "RVpFr8xbeiWRnj8cu7AeOP"
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

private const val testInputPassword = "password"
