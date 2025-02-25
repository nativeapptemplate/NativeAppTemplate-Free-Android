package com.nativeapptemplate.nativeapptemplatefree.ui.settings

import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestLoginRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestSignUpRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.emptyUserData
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
class ShopkeeperEditViewModelTest {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val loginRepository = TestLoginRepository()
  private val signUpRepository = TestSignUpRepository()

  private lateinit var viewModel: ShopkeeperEditViewModel

  @Before
  fun setUp() {
    viewModel = ShopkeeperEditViewModel(
      loginRepository = loginRepository,
      signUpRepository = signUpRepository,
    )
  }

  @Test
  fun stateIsInitiallyLoading() = runTest {
    assertTrue(viewModel.uiState.value.isLoading)
  }

  @Test
  fun stateUserData_whenSuccess_matchesUserDataFromRepository() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    loginRepository.sendUserData(emptyUserData)

    viewModel.reload()
    val uiStateValue = viewModel.uiState.value
    assertTrue(uiStateValue.success)
    assertFalse(uiStateValue.isLoading)

    val userDataFromRepository = loginRepository.userData.first()
    assertEquals(userDataFromRepository, uiStateValue.userData)
  }

  @Test
  fun shopkeeper_whenUpdatingShopkeeper_isSavedInPreference() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    val userData = emptyUserData.copy(
      name = testInputLoggedInShopkeeper.getName()!!,
      email = testInputLoggedInShopkeeper.getEmail()!!,
      timeZone = testInputLoggedInShopkeeper.getTimeZone()!!,
    )

    val newTestInputLoggedInShopkeeper = LoggedInShopkeeper(
      datum = Data(
        id = LOGGED_IN_SHOPKEEPER_ID,
        type = LOGGED_IN_SHOPKEEPER_TYPE,
        attributes = testInputLoggedInShopkeeper.datum!!.attributes!!.copy(
          name = testInputNewName,
          timeZone = testInputNewTimeZone
        )
      )
    )

    loginRepository.sendUserData(userData)
    signUpRepository.sendLoggedInShopkeeper(newTestInputLoggedInShopkeeper)

    viewModel.reload()
    viewModel.updateName(testInputNewName)
    viewModel.updateTimeZone(testInputNewTimeZone)

    assertEquals(viewModel.uiState.value.name, testInputNewName)
    assertEquals(viewModel.uiState.value.timeZone, testInputNewTimeZone)
    assertFalse(viewModel.hasInvalidData())

    viewModel.updateShopkeeper()

    val userDataFromLoginRepository = loginRepository.userData.first()
    assertEquals(userDataFromLoginRepository.name, testInputNewName)
    assertEquals(userDataFromLoginRepository.timeZone, testInputNewTimeZone)
    assertFalse(userDataFromLoginRepository.isEmailUpdated)

    val uiStateValue = viewModel.uiState.value
    assertTrue(uiStateValue.isUpdated)
    assertFalse(uiStateValue.isEmailUpdated)
    assertFalse(uiStateValue.isLoading)
  }

  @Test
  fun stateIsEmailUpdated_whenUpdatingShopkeeperEmail_becomesTrue() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    val userData = emptyUserData.copy(
      name = testInputLoggedInShopkeeper.getName()!!,
      email = testInputLoggedInShopkeeper.getEmail()!!,
      timeZone = testInputLoggedInShopkeeper.getTimeZone()!!,
    )

    val newTestInputLoggedInShopkeeper = LoggedInShopkeeper(
      datum = Data(
        id = LOGGED_IN_SHOPKEEPER_ID,
        type = LOGGED_IN_SHOPKEEPER_TYPE,
        attributes = testInputLoggedInShopkeeper.datum!!.attributes!!.copy(
          name = testInputNewName,
          email = testInputNewEmail,
          timeZone = testInputNewTimeZone
        )
      )
    )

    loginRepository.sendUserData(userData)
    signUpRepository.sendLoggedInShopkeeper(newTestInputLoggedInShopkeeper)

    viewModel.reload()
    viewModel.updateName(testInputNewName)
    viewModel.updateEmail(testInputNewEmail)
    viewModel.updateTimeZone(testInputNewTimeZone)

    assertEquals(viewModel.uiState.value.name, testInputNewName)
    assertEquals(viewModel.uiState.value.email, testInputNewEmail)
    assertEquals(viewModel.uiState.value.timeZone, testInputNewTimeZone)
    assertFalse(viewModel.hasInvalidData())

    viewModel.updateShopkeeper()

    val userDataFromLoginRepository = loginRepository.userData.first()
    assertEquals(userDataFromLoginRepository.name, testInputNewName)
    assertEquals(userDataFromLoginRepository.email, testInputNewEmail)
    assertEquals(userDataFromLoginRepository.timeZone, testInputNewTimeZone)
    assertTrue(userDataFromLoginRepository.isEmailUpdated)

    val uiStateValue = viewModel.uiState.value
    assertTrue(uiStateValue.isEmailUpdated)
  }

  @Test
  fun isMyAccountDeleted_whenDeletingShopkeeper_becomesTrue() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    val userData = emptyUserData.copy(
      name = testInputLoggedInShopkeeper.getName()!!,
      email = testInputLoggedInShopkeeper.getEmail()!!,
      timeZone = testInputLoggedInShopkeeper.getTimeZone()!!,
    )

    loginRepository.sendUserData(userData)

    viewModel.reload()
    viewModel.deleteShopkeeper()

    val userDataFromLoginRepository = loginRepository.userData.first()
    assertTrue(userDataFromLoginRepository.isMyAccountDeleted)

    val uiStateValue = viewModel.uiState.value
    assertTrue(uiStateValue.isDeleted)
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
  fun updating_withoutChanges_isInvalid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    val userData = emptyUserData.copy(
      name = testInputLoggedInShopkeeper.getName()!!,
      email = testInputLoggedInShopkeeper.getEmail()!!,
      timeZone = testInputLoggedInShopkeeper.getTimeZone()!!,
    )

    loginRepository.sendUserData(userData)

    viewModel.reload()

    assertTrue(viewModel.hasInvalidData())
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

private const val testInputNewName = "Olivia Clark"
private const val testInputNewEmail = "olivia@example.com"
private const val testInputNewTimeZone = "Hawaii"
