package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTags
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestShopRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.util.MainDispatcherRule
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagListRoute
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
class ItemTagListViewModelTest {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val shopRepository = TestShopRepository()
  private val itemTagRepository = TestItemTagRepository()

  private lateinit var viewModel: ItemTagListViewModel

  @Before
  fun setUp() {
    viewModel = ItemTagListViewModel(
      savedStateHandle = SavedStateHandle(
        route = ItemTagListRoute(shopId = testInputShop.datum!!.id!!),
      ),
      shopRepository = shopRepository,
      itemTagRepository = itemTagRepository,
    )
  }

  @Test
  fun stateIsInitiallyLoading() = runTest {
    assertTrue(viewModel.uiState.value.isLoading)
  }

  @Test
  fun isEmpty_whenItemTagsIsMissing_isTrue() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    shopRepository.sendShop(testInputShop)
    itemTagRepository.sendItemTags(ItemTags())

    viewModel.reload()

    assertTrue(viewModel.isEmpty().first())
  }

  @Test
  fun stateShop_whenSuccess_matchesShopFromRepository() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    shopRepository.sendShop(testInputShop)
    itemTagRepository.sendItemTags(testInputItemTags)

    viewModel.reload()
    val uiStateValue = viewModel.uiState.value
    assertTrue(uiStateValue.success)
    assertFalse(uiStateValue.isLoading)

    val shopId = testInputShop.datum!!.id!!

    val shopFromRepository = shopRepository.getShop(shopId).first()
    val itemTagsFromRepository = itemTagRepository.getItemTags(shopId).first()

    assertEquals(shopFromRepository, uiStateValue.shop)
    assertEquals(itemTagsFromRepository, uiStateValue.itemTags)
  }

  @Test
  fun stateIsLoading_whenDeletingItemTag_becomesFalse() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    shopRepository.sendShop(testInputShop)
    itemTagRepository.sendItemTags(testInputItemTags)
    itemTagRepository.sendItemTag(testInputItemTag)

    viewModel.reload()
    viewModel.deleteItemTag(testInputItemTags.datum.first().id!!)

    val uiStateValue = viewModel.uiState.value
    assertFalse(uiStateValue.isLoading)
  }
}

private const val SHOP_TYPE = "shop"
private const val SHOP_ID = "5712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val SHOP_NAME = "8th & Townsend"
private const val SHOP_DESCRIPTION = "This is a shop."
private const val SHOP_TIME_ZONE = "Pacific Time (US & Canada)"

private var testInputShopsData =
  Data(
    id = SHOP_ID,
    type = SHOP_TYPE,
    attributes = Attributes(
      name = SHOP_NAME,
      description = SHOP_DESCRIPTION,
      timeZone = SHOP_TIME_ZONE,
      completedItemTagsCount = 3,
    )
  )

private var testInputShop = Shop(
  datum = testInputShopsData,
)

private const val ITEM_TAG_TYPE = "item_tag"
private const val ITEM_TAG_1_ID = "9712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val ITEM_TAG_2_ID = "9712F2DF-DFC7-A3AA-66BC-191203654A1B"
private const val ITEM_TAG_3_ID = "9712F2DF-DFC7-A3AA-66BC-191203654A1C"
private const val ITEM_TAG_1_QUEUE_NUMBER = "A001"
private const val ITEM_TAG_2_QUEUE_NUMBER = "A002"
private const val ITEM_TAG_3_QUEUE_NUMBER = "A003"
private const val ITEM_TAG_STATE = "idled"
private const val ITEM_TAG_SCAN_STATE = "unscanned"
private const val ITEM_TAG_CREATED_AT = "2025-01-02T12:00:00.000Z"
private const val ITEM_TAG_CUSTOMER_READ_AT = "2025-01-02T12:00:01.000Z"
private const val ITEM_TAG_COMPLETED_AT = "2025-01-02T12:00:03.000Z"
private const val ITEM_TAG_ALREADY_COMPLETED = false

private val testInputItemTagsData = listOf(
  Data(
    id = ITEM_TAG_1_ID,
    type = ITEM_TAG_TYPE,
    attributes = Attributes(
      shopId = SHOP_ID,
      queueNumber = ITEM_TAG_1_QUEUE_NUMBER,
      state = ITEM_TAG_STATE,
      scanState = ITEM_TAG_SCAN_STATE,
      createdAt = ITEM_TAG_CREATED_AT,
      shopName = SHOP_NAME,
      customerReadAt = ITEM_TAG_CUSTOMER_READ_AT,
      completedAt = ITEM_TAG_COMPLETED_AT,
      alreadyCompleted = ITEM_TAG_ALREADY_COMPLETED
    )
  ),
  Data(
    id = ITEM_TAG_2_ID,
    type = ITEM_TAG_TYPE,
    attributes = Attributes(
      shopId = SHOP_ID,
      queueNumber = ITEM_TAG_2_QUEUE_NUMBER,
      state = ITEM_TAG_STATE,
      scanState = ITEM_TAG_SCAN_STATE,
      createdAt = ITEM_TAG_CREATED_AT,
      shopName = SHOP_NAME,
      customerReadAt = ITEM_TAG_CUSTOMER_READ_AT,
      alreadyCompleted = ITEM_TAG_ALREADY_COMPLETED
    )
  ),
  Data(
    id = ITEM_TAG_3_ID,
    type = ITEM_TAG_TYPE,
    attributes = Attributes(
      shopId = SHOP_ID,
      queueNumber = ITEM_TAG_3_QUEUE_NUMBER,
      state = ITEM_TAG_STATE,
      scanState = ITEM_TAG_SCAN_STATE,
      createdAt = ITEM_TAG_CREATED_AT,
      shopName = SHOP_NAME,
      customerReadAt = ITEM_TAG_CUSTOMER_READ_AT,
      completedAt = ITEM_TAG_COMPLETED_AT,
      alreadyCompleted = ITEM_TAG_ALREADY_COMPLETED
    )
  ),
)

private val testInputItemTags = ItemTags(
  datum = testInputItemTagsData,
)

private val testInputItemTag = ItemTag(
  datum = testInputItemTagsData.first(),
)
