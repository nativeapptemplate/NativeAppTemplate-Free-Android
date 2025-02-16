package com.nativeapptemplate.nativeapptemplatefree.ui.settings

import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestLoginRepository
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

class SettingsViewModelTest {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val loginRepository = TestLoginRepository()

  private lateinit var viewModel: SettingsViewModel

  @Before
  fun setUp() {
    viewModel = SettingsViewModel(
      loginRepository = loginRepository,
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
  fun stateIsLoading_whenLoggingOut_becomesFalse() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    loginRepository.sendUserData(emptyUserData)

    viewModel.reload()
    viewModel.logout()

    val uiStateValue = viewModel.uiState.value
    assertFalse(uiStateValue.isLoading)
  }

  @Test
  fun stateMessage_whenUpdatingMessage_becomesNewMessage() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    loginRepository.sendUserData(emptyUserData)

    viewModel.reload()
    val newMessage = "New Message"
    viewModel.updateMessage(newMessage)

    val uiStateValue = viewModel.uiState.value
    assertEquals(uiStateValue.message, newMessage)
  }
}
