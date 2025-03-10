package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.nativeapptemplate.nativeapptemplatefree.testing.util.MainDispatcherRule
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagWriteRoute
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility
import kotlinx.coroutines.flow.collect
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

/**
 * These tests use Robolectric because the subject under test (the ViewModel) uses
 * `SavedStateHandle.toRoute` which has a dependency on `android.os.Bundle`.
 *
 * TODO: Remove Robolectric if/when AndroidX Navigation API is updated to remove Android dependency.
 *  *  See b/340966212.
 */
@RunWith(RobolectricTestRunner::class)
class ItemTagWriteViewModelTest {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private lateinit var viewModel: ItemTagWriteViewModel

  @Before
  fun setUp() {
    viewModel = ItemTagWriteViewModel(
      savedStateHandle = SavedStateHandle(
        route = ItemTagWriteRoute(
          id = testInputItemTagId,
          isLock = testInputIsLock,
          itemTagType = testInputItemTagType
        ),
      ),
    )
  }

  @Test
  fun returnScanUri() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
    assertEquals(
      viewModel.scanUri,
      Utility.scanUri(itemTagId = testInputItemTagId, itemTagType = testInputItemTagType)
    )
  }

  @Test
  fun stateIsUpdated_isUpdated() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updateIsUpdated(true)
    assertTrue(viewModel.uiState.value.isUpdated)

    viewModel.updateIsUpdated(false)
    assertFalse(viewModel.uiState.value.isUpdated)
  }

  @Test
  fun stateIsFailed_isUpdated() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updateIsFailed(true)
    assertTrue(viewModel.uiState.value.isFailed)

    viewModel.updateIsFailed(false)
    assertFalse(viewModel.uiState.value.isFailed)
  }

  @Test
  fun stateMessage_isUpdated() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    val newMessage = "new message"
    viewModel.updateMessage(newMessage)

    val uiStateValue = viewModel.uiState.value
    assertEquals(uiStateValue.message, newMessage)
  }
}

private const val testInputItemTagId = "9712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val testInputIsLock = false
private const val testInputItemTagType = "server"
