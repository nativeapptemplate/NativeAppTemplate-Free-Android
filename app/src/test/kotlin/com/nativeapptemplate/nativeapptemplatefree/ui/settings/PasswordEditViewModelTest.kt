package com.nativeapptemplate.nativeapptemplatefree.ui.settings

import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestAccountPasswordRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PasswordEditViewModelTest {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val accountPasswordRepository = TestAccountPasswordRepository()

  private lateinit var viewModel: PasswordEditViewModel

  @Before
  fun setUp() {
    viewModel = PasswordEditViewModel(
      accountPasswordRepository = accountPasswordRepository,
    )
  }

  @Test
  fun stateIsInitiallyNotLoading() = runTest {
    assertFalse(viewModel.uiState.value.isLoading)
  }

  @Test
  fun stateIsUpdated_whenUpdatingPassword_becomesTrue() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updateCurrentPassword(testInputCurrentPassword)
    viewModel.updatePassword(testInputNewPassword)
    viewModel.updatePasswordConfirmation(testInputNewPassword)

    assertEquals(viewModel.uiState.value.currentPassword, testInputCurrentPassword)
    assertEquals(viewModel.uiState.value.password, testInputNewPassword)
    assertEquals(viewModel.uiState.value.passwordConfirmation, testInputNewPassword)
    assertFalse(viewModel.hasInvalidData())

    viewModel.updatePassword()

    val uiStateValue = viewModel.uiState.value
    assertTrue(uiStateValue.isUpdated)
  }

  @Test
  fun blankPassword_isInvalid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updatePassword("")

    assertTrue(viewModel.hasInvalidDataPassword())
  }

  @Test
  fun blankCurrentPassword_isInvalid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updateCurrentPassword("")

    assertTrue(viewModel.hasInvalidData())
  }

  @Test
  fun blankPasswordConfirmation_isInvalid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updatePasswordConfirmation("")

    assertTrue(viewModel.hasInvalidData())
  }

  @Test
  fun passwordWithIncorrectLength_isInvalid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updatePassword("1234567")

    assertTrue(viewModel.hasInvalidDataPassword())
  }
}

private const val testInputCurrentPassword = "password"
private const val testInputNewPassword = "newPassword"