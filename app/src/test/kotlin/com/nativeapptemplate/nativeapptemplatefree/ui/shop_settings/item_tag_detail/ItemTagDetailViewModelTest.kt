package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.util.MainDispatcherRule
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagDetailRoute
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

/**
 * These tests use Robolectric because the subject under test (the ViewModel) uses
 * `SavedStateHandle.toRoute` which has a dependency on `android.os.Bundle`.
 *
 * TODO: Remove Robolectric if/when AndroidX Navigation API is updated to remove Android dependency.
 *  *  See b/340966212.
 */
@RunWith(RobolectricTestRunner::class)
class ItemTagDetailViewModelTest {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val itemTagRepository = TestItemTagRepository()

  private lateinit var viewModel: ItemTagDetailViewModel

  @Before
  fun setUp() {
    viewModel = ItemTagDetailViewModel(
      savedStateHandle = SavedStateHandle(
        route = ItemTagDetailRoute(id = testInputItemTag.datum!!.id!!),
      ),
      itemTagRepository = itemTagRepository,
    )
  }

  @Test
  fun stateIsInitiallyLoading() = runTest {
    assertTrue(viewModel.uiState.value.isLoading)
  }

  @Test
  fun stateItemTag_whenSuccess_matchesItemTagFromRepository() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    itemTagRepository.sendItemTag((testInputItemTag))

    viewModel.reload()
    val uiStateValue = viewModel.uiState.value
    assertTrue(uiStateValue.success)
    assertFalse(uiStateValue.isLoading)

    val itemTagFromRepository = itemTagRepository.getItemTag(testInputItemTag.datum!!.id!!).first()

    assertEquals(itemTagFromRepository, uiStateValue.itemTag)
  }

  @Test
  fun stateIsDeleted_whenDeletingItemTag_becomesTrue() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    itemTagRepository.sendItemTag(testInputItemTag)

    viewModel.reload()
    viewModel.deleteItemTag()

    val uiStateValue = viewModel.uiState.value
    assertTrue(uiStateValue.isDeleted)
  }

  @Test
  fun stateIsLock_isUpdated() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    itemTagRepository.sendItemTag(testInputItemTag)

    viewModel.reload()

    viewModel.updateIsLock(true)
    assertTrue(viewModel.uiState.value.isLock)

    viewModel.updateIsLock(false)
    assertFalse(viewModel.uiState.value.isLock)
  }

  @Test
  fun stateMessage_isUpdated() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    itemTagRepository.sendItemTag(testInputItemTag)

    viewModel.reload()
    val newMessage = "new message"
    viewModel.updateMessage(newMessage)

    val uiStateValue = viewModel.uiState.value
    assertEquals(uiStateValue.message, newMessage)
  }
}

private const val SHOP_ID = "5712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val SHOP_NAME = "8th & Townsend"

private const val ITEM_TAG_TYPE = "item_tag"
private const val ITEM_TAG_ID = "9712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val ITEM_TAG_QUEUE_NUMBER = "A001"
private const val ITEM_TAG_STATE = "idled"
private const val ITEM_TAG_SCAN_STATE = "unscanned"
private const val ITEM_TAG_CREATED_AT = "2025-01-02T12:00:00.000Z"
private const val ITEM_TAG_CUSTOMER_READ_AT = "2025-01-02T12:00:01.000Z"
private const val ITEM_TAG_COMPLETED_AT = "2025-01-02T12:00:03.000Z"
private const val ITEM_TAG_ALREADY_COMPLETED = false

private val testInputItemTagData =
  Data(
    id = ITEM_TAG_ID,
    type = ITEM_TAG_TYPE,
    attributes = Attributes(
      shopId = SHOP_ID,
      queueNumber = ITEM_TAG_QUEUE_NUMBER,
      state = ITEM_TAG_STATE,
      scanState = ITEM_TAG_SCAN_STATE,
      createdAt = ITEM_TAG_CREATED_AT,
      shopName = SHOP_NAME,
      customerReadAt = ITEM_TAG_CUSTOMER_READ_AT,
      completedAt = ITEM_TAG_COMPLETED_AT,
      alreadyCompleted = ITEM_TAG_ALREADY_COMPLETED
    )
  )

private val testInputItemTag = ItemTag(
  datum = testInputItemTagData,
)
