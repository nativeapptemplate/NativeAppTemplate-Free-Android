package com.nativeapptemplate.nativeapptemplatefree.ui.scan

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestLoginRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.emptyUserData
import com.nativeapptemplate.nativeapptemplatefree.testing.util.MainDispatcherRule
import com.nativeapptemplate.nativeapptemplatefree.ui.scan.navigation.DoScanRoute
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * These tests use Robolectric because the subject under test (the ViewModel) uses
 * `SavedStateHandle.toRoute` which has a dependency on `android.os.Bundle`.
 *
 * TODO: Remove Robolectric if/when AndroidX Navigation API is updated to remove Android dependency.
 *  *  See b/340966212.
 */
@RunWith(RobolectricTestRunner::class)
class DoScanViewModelTestIsTestTrue {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val loginRepository = TestLoginRepository()

  private lateinit var viewModel: DoScanViewModel

  @Before
  fun setUp() {
    viewModel = DoScanViewModel(
      savedStateHandle = SavedStateHandle(
        route = DoScanRoute(isTest = true),
      ),
      loginRepository = loginRepository,
    )
  }

  @Test
  fun stateIsInitiallyLoading() = runTest {
    assertFalse(viewModel.uiState.value.isLoading)
  }

  @Test
  fun stateIsScanned_updated() = runTest {
    loginRepository.sendUserData(emptyUserData)

    viewModel.updateIsScanned(true)

    assertTrue(viewModel.uiState.value.isScanned)
  }

  @Test
  fun scanViewSelectedTabIndex_isSavedInPreference() = runTest {
    loginRepository.sendUserData(emptyUserData)

    viewModel.updateScanViewSelectedTabIndex()

    assertEquals(loginRepository.userData.first().scanViewSelectedTabIndex, 1)
  }

  @Test
  fun shouldFetchItemTagForShowTagInfoScan_isSavedInPreference() = runTest {
    loginRepository.sendUserData(emptyUserData)

    viewModel.updateExecFlagAfterScanning(true)

    assertTrue(loginRepository.userData.first().shouldFetchItemTagForShowTagInfoScan)
  }
}

/**
 * These tests use Robolectric because the subject under test (the ViewModel) uses
 * `SavedStateHandle.toRoute` which has a dependency on `android.os.Bundle`.
 *
 * TODO: Remove Robolectric if/when AndroidX Navigation API is updated to remove Android dependency.
 *  *  See b/340966212.
 */
@RunWith(RobolectricTestRunner::class)
class DoScanViewModelTestIsTestFalse {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val loginRepository = TestLoginRepository()

  private lateinit var viewModel: DoScanViewModel

  @Before
  fun setUp() {
    viewModel = DoScanViewModel(
      savedStateHandle = SavedStateHandle(
        route = DoScanRoute(isTest = false),
      ),
      loginRepository = loginRepository,
    )
  }

  @Test
  fun scanViewSelectedTabIndex_isSavedInPreference() = runTest {
    loginRepository.sendUserData(emptyUserData)

    viewModel.updateScanViewSelectedTabIndex()

    assertEquals(loginRepository.userData.first().scanViewSelectedTabIndex, 0)
  }

  @Test
  fun shouldCompleteItemTagForCompleteScan_isSavedInPreference() = runTest {
    loginRepository.sendUserData(emptyUserData)

    viewModel.updateExecFlagAfterScanning(true)

    assertTrue(loginRepository.userData.first().shouldCompleteItemTagForCompleteScan)
  }
}
