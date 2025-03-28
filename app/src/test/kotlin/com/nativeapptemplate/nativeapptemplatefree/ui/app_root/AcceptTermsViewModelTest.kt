package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestLoginRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.emptyUserData
import com.nativeapptemplate.nativeapptemplatefree.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AcceptTermsViewModelTest {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val loginRepository = TestLoginRepository()

  private lateinit var viewModel: AcceptTermsViewModel

  @Before
  fun setUp() {
    viewModel = AcceptTermsViewModel(
      loginRepository = loginRepository,
    )
  }

  @Test
  fun stateIsInitiallyLoading() = runTest {
    assertFalse(viewModel.uiState.value.isLoading)
  }

  @Test
  fun stateIsUpdated_whenUpdatingConfirmedTermsVersion_becomesTrue() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    loginRepository.sendUserData(emptyUserData)
    viewModel.updateConfirmedTermsVersion()

    val uiStateValue = viewModel.uiState.value
    assertTrue(uiStateValue.isUpdated)
  }
}
