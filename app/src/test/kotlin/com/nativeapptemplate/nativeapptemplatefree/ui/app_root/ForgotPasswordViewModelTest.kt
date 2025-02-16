package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

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
class ForgotPasswordViewModelTest {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val signUpRepository = TestSignUpRepository()

  private lateinit var viewModel: ForgotPasswordViewModel

  @Before
  fun setUp() {
    viewModel = ForgotPasswordViewModel(
      signUpRepository = signUpRepository,
    )
  }

  @Test
  fun stateIsInitiallyNotLoading() = runTest {
    assertFalse(viewModel.uiState.value.isLoading)
  }

  @Test
  fun stateIsSent_whenSendingResetPasswordInstructions_becomesTrue() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    val email = "john@example.com"
    viewModel.updateEmail(email)

    assertEquals(viewModel.uiState.value.email, email)
    assertFalse(viewModel.hasInvalidData())

    viewModel.sendMeResetPasswordInstructions()

    val uiStateValue = viewModel.uiState.value
    assertTrue(uiStateValue.isSent)
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
}
