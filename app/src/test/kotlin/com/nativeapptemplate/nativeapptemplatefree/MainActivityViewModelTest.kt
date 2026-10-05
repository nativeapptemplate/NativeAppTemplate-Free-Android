package com.nativeapptemplate.nativeapptemplatefree

import com.nativeapptemplate.nativeapptemplatefree.common.errors.ApiException
import com.nativeapptemplate.nativeapptemplatefree.model.UserData
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestLoginRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Robolectric so android.util.Log works in the failure paths.
@RunWith(RobolectricTestRunner::class)
class MainActivityViewModelTest {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val loginRepository = TestLoginRepository()

  private lateinit var viewModel: MainActivityViewModel

  @Before
  fun setUp() {
    viewModel = MainActivityViewModel(loginRepository = loginRepository)
  }

  @Test
  fun uiState_initialValue_isLoading() = runTest {
    assertEquals(MainActivityUiState.Loading, viewModel.uiState.value)
    assertFalse(viewModel.uiState.value.isLoggedIn)
  }

  @Test
  fun uiState_emitsSuccess_whenUserDataArrives() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    val userData = UserData(isLoggedIn = true, email = "john@example.com")
    loginRepository.sendUserData(userData)

    val state = viewModel.uiState.value
    assertTrue(state is MainActivityUiState.Success)
    assertEquals(userData, (state as MainActivityUiState.Success).userData)
    assertTrue(state.isLoggedIn)
  }

  @Test
  fun uiState_isLoggedInReflectsUserData() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    loginRepository.sendUserData(UserData(isLoggedIn = false))
    assertFalse(viewModel.uiState.value.isLoggedIn)

    loginRepository.sendUserData(UserData(isLoggedIn = true))
    assertTrue(viewModel.uiState.value.isLoggedIn)
  }

  @Test
  fun updateDidShowTapShopBelowTip_persistsValue() = runTest {
    loginRepository.sendUserData(UserData())

    viewModel.updateDidShowTapShopBelowTip(true)

    assertTrue(loginRepository.userData.first().didShowTapShopBelowTip)
  }

  @Test
  fun updatePermissions_whenOffline_keepsTheUserSignedIn() = runTest {
    loginRepository.sendIsLoggedIn(true)
    // What throwApiError produces when the request never reaches the server.
    loginRepository.failPermissions(ApiException.UnprocessableError(rawMessage = "Unable to resolve host"))

    viewModel.updatePermissions()

    assertEquals(0, loginRepository.logoutCallCount)
  }

  @Test
  fun updatePermissions_whenServerFails_keepsTheUserSignedIn() = runTest {
    loginRepository.sendIsLoggedIn(true)
    loginRepository.failPermissions(ApiException.UnprocessableError(rawMessage = "Bad Gateway", httpStatusCode = 502))

    viewModel.updatePermissions()

    assertEquals(0, loginRepository.logoutCallCount)
  }

  @Test
  fun updatePermissions_whenSessionIsRejected_logsOut() = runTest {
    loginRepository.sendIsLoggedIn(true)
    loginRepository.failPermissions(ApiException.UnprocessableError(rawMessage = "Unauthorized", httpStatusCode = 401))

    viewModel.updatePermissions()

    assertEquals(1, loginRepository.logoutCallCount)
  }
}
