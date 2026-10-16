package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasScrollToKeyAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToKey
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTags
import com.nativeapptemplate.nativeapptemplatefree.model.Meta
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestShopRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.util.MainDispatcherRule
import com.nativeapptemplate.nativeapptemplatefree.testing.util.TEST_API_ERROR_MESSAGE
import com.nativeapptemplate.nativeapptemplatefree.testing.util.testApiError
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
import org.robolectric.RuntimeEnvironment

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
  val composeTestRule = createComposeRule()

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

    assertTrue(viewModel.isEmpty.first())
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

    // The fixtures the test repositories served.
    assertEquals(testInputShop, uiStateValue.shop)
    assertEquals(testInputItemTagsData, uiStateValue.itemTags)
  }

  // Without "meta" the loaded page counts as the only one: currentPage = the requested page (1),
  // totalPages = 1, so there is nothing more to load.
  @Test
  fun reload_withoutPaginationMeta_treatsThePageAsTheLast() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
    shopRepository.sendShop(testInputShop)
    itemTagRepository.sendItemTags(ItemTags(datum = testInputItemTagsData, meta = null))

    viewModel.reload()
    viewModel.loadMore()

    val uiStateValue = viewModel.uiState.value
    assertEquals(1, uiStateValue.currentPage)
    assertEquals(1, uiStateValue.totalPages)
    assertFalse(uiStateValue.hasMorePages)
    assertEquals(listOf<Int?>(1), itemTagRepository.requestedPages)
  }

  // A failed reload (page 1) is not a failed next page: it shows the error and leaves loading more allowed.
  @Test
  fun reload_whenItFails_showsTheErrorWithoutBlockingLoadMore() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
    shopRepository.sendShop(testInputShop)
    itemTagRepository.fail(ItemTagRepository::getItemTags, testApiError)

    viewModel.reload()

    val uiStateValue = viewModel.uiState.value
    assertEquals(TEST_API_ERROR_MESSAGE, uiStateValue.message)
    assertFalse(uiStateValue.isLoading)
    assertFalse(uiStateValue.success)
    assertFalse(uiStateValue.loadMoreFailed)
    assertEquals(emptyList<Data>(), uiStateValue.itemTags)
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

  @Test
  fun paginationMeta_whenPresent_isReflectedInUiState() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    shopRepository.sendShop(testInputShop)
    itemTagRepository.sendItemTags(testInputItemTagsPage1)

    viewModel.reload()

    val uiStateValue = viewModel.uiState.value
    assertEquals(1, uiStateValue.currentPage)
    assertEquals(2, uiStateValue.totalPages)
    assertTrue(uiStateValue.hasMorePages)
  }

  @Test
  fun loadMore_accumulatesItemsFromNextPage() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    shopRepository.sendShop(testInputShop)
    itemTagRepository.sendItemTags(testInputItemTagsPage1)

    viewModel.reload()
    assertEquals(2, viewModel.uiState.value.itemTags.size)

    itemTagRepository.sendItemTags(testInputItemTagsPage2)
    viewModel.loadMore()

    val uiStateValue = viewModel.uiState.value
    assertEquals(3, uiStateValue.itemTags.size)
    assertEquals(2, uiStateValue.currentPage)
    assertEquals(2, uiStateValue.totalPages)
    assertFalse(uiStateValue.hasMorePages)
  }

  // A tag added at the front of the list between page loads pushes tag 2 from page 1 onto page 2,
  // so page 2 returns [tag 2, tag 3]. The list keeps one tag 2, where page 1 put it:
  // [1, 2] + [2, 3] without the repeated 2 = [1, 2, 3].
  @Test
  fun loadMore_whenTheListShiftedBetweenPages_keepsEachTagOnceInOrder() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    shopRepository.sendShop(testInputShop)
    itemTagRepository.sendItemTags(testInputItemTagsPage1)
    viewModel.reload()

    itemTagRepository.sendItemTags(
      ItemTags(
        datum = listOf(testInputItemTagsData[1], testInputItemTagsData[2]),
        meta = Meta(currentPage = 2, totalPages = 2, totalCount = 4, limit = 2),
      ),
    )
    viewModel.loadMore()

    assertEquals(
      listOf(ITEM_TAG_1_ID, ITEM_TAG_2_ID, ITEM_TAG_3_ID),
      viewModel.uiState.value.itemTags.map { it.id },
    )
  }

  @Test
  fun loadMore_doesNotFire_whenNoMorePages() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    shopRepository.sendShop(testInputShop)
    itemTagRepository.sendItemTags(testInputItemTagsPage2)

    viewModel.reload()

    val itemCountBefore = viewModel.uiState.value.itemTags.size
    viewModel.loadMore()
    assertEquals(itemCountBefore, viewModel.uiState.value.itemTags.size)
    assertFalse(viewModel.uiState.value.isLoadingMore)
  }

  @Test
  fun reload_resetsToFirstPage() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    shopRepository.sendShop(testInputShop)
    itemTagRepository.sendItemTags(testInputItemTagsPage1)

    viewModel.reload()
    assertEquals(2, viewModel.uiState.value.itemTags.size)

    itemTagRepository.sendItemTags(testInputItemTagsPage2)
    viewModel.loadMore()
    assertEquals(3, viewModel.uiState.value.itemTags.size)

    itemTagRepository.sendItemTags(testInputItemTagsPage1)
    viewModel.reload()

    val uiStateValue = viewModel.uiState.value
    assertEquals(2, uiStateValue.itemTags.size)
    assertEquals(1, uiStateValue.currentPage)
  }

  @Test
  fun scrollingToTheEnd_keepsLoadingPagesBeyondThePageTwo() {
    // 4 pages x 10 tags, so each scroll to the end must request the next page.
    itemTagRepository.itemTagsForPage = { page -> pagedItemTags(page = page ?: 1, totalPages = 4, perPage = 10) }
    shopRepository.sendShop(testInputShop)
    composeTestRule.setContent {
      val uiState by viewModel.uiState.collectAsState()
      ItemTagListView(
        viewModel = viewModel,
        uiState = uiState,
        onItemClick = {},
        onAddItemTagClick = {},
        onBackClick = {},
      )
    }
    composeTestRule.runOnIdle { viewModel.reload() }

    val list = composeTestRule.onNode(hasScrollToIndexAction())
    list.performScrollToIndex(9) // last tag of page 1
    composeTestRule.waitForIdle()
    list.performScrollToIndex(19) // last tag of page 2
    composeTestRule.waitForIdle()

    assertEquals(listOf<Int?>(1, 2, 3), itemTagRepository.requestedPages)
  }

  // Each tag's row is keyed by the tag's id, so Compose keeps a row's state (e.g. a revealed swipe
  // action) with its tag when the list changes. pagedItemTags names tag n "item-tag-n" with name "A00n".
  @Test
  fun tagRows_areKeyedByTheTagId() {
    itemTagRepository.itemTagsForPage = { page -> pagedItemTags(page = page ?: 1, totalPages = 1, perPage = 10) }
    shopRepository.sendShop(testInputShop)
    composeTestRule.setContent {
      val uiState by viewModel.uiState.collectAsState()
      ItemTagListView(
        viewModel = viewModel,
        uiState = uiState,
        onItemClick = {},
        onAddItemTagClick = {},
        onBackClick = {},
      )
    }
    composeTestRule.runOnIdle { viewModel.reload() }

    composeTestRule.onNode(hasScrollToKeyAction()).performScrollToKey("item-tag-7")

    composeTestRule.onNodeWithText("A007").assertExists()
  }

  // The last row (and the Delete button its swipe reveals) must be able to scroll above the "+"
  // button; without bottom content padding the list ends behind the button and the row stays under it.
  @Test
  fun lastRow_scrollsAboveTheAddButton() {
    // 20 tags on one page: longer than the screen, so the list scrolls.
    itemTagRepository.itemTagsForPage = { page -> pagedItemTags(page = page ?: 1, totalPages = 1, perPage = 20) }
    shopRepository.sendShop(testInputShop)
    composeTestRule.setContent {
      val uiState by viewModel.uiState.collectAsState()
      ItemTagListView(
        viewModel = viewModel,
        uiState = uiState,
        onItemClick = {},
        onAddItemTagClick = {},
        onBackClick = {},
      )
    }
    composeTestRule.runOnIdle { viewModel.reload() }

    // Scrolling to the last tag scrolls the list to its end. pagedItemTags names tag 20 "A020".
    composeTestRule.onNode(hasScrollToKeyAction()).performScrollToKey("item-tag-20")
    val context = RuntimeEnvironment.getApplication()
    val lastRowBottom = composeTestRule.onNodeWithText("A020").getUnclippedBoundsInRoot().bottom
    val addButtonTop = composeTestRule.onNodeWithContentDescription(context.getString(R.string.label_add_item_tag))
      .getUnclippedBoundsInRoot().top

    assertTrue("last row ends at $lastRowBottom, the add button starts at $addButtonTop", lastRowBottom <= addButtonTop)
  }

  // The API rejects it: the coded error is shown, the spinner stops, and the list stays.
  @Test
  fun deleteItemTag_whenTheApiRejectsIt_showsTheErrorAndKeepsTheList() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
    shopRepository.sendShop(testInputShop)
    itemTagRepository.sendItemTags(testInputItemTags)
    viewModel.reload()
    itemTagRepository.fail(ItemTagRepository::deleteItemTag, testApiError)

    viewModel.deleteItemTag(testInputItemTags.datum.first().id!!)

    val uiStateValue = viewModel.uiState.value
    assertEquals(TEST_API_ERROR_MESSAGE, uiStateValue.message)
    assertFalse(uiStateValue.isLoading)
    assertEquals(testInputItemTags.datum.map { it.id }, uiStateValue.itemTags.map { it.id })
  }

  // A failed next page shows the coded error, keeps page 1 (2 tags), and leaves page 2 to load after a reload.
  @Test
  fun loadMore_whenTheNextPageFails_keepsPageOneAndCanTryAgain() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
    shopRepository.sendShop(testInputShop)
    itemTagRepository.sendItemTags(testInputItemTagsPage1)
    viewModel.reload()
    itemTagRepository.fail(ItemTagRepository::getItemTags, testApiError)

    viewModel.loadMore()

    val uiStateValue = viewModel.uiState.value
    assertEquals(TEST_API_ERROR_MESSAGE, uiStateValue.message)
    assertFalse(uiStateValue.isLoading)
    assertFalse(uiStateValue.isLoadingMore)
    assertEquals(testInputItemTagsPage1.datum.map { it.id }, uiStateValue.itemTags.map { it.id })
    assertTrue(uiStateValue.hasMorePages)
  }

  // ItemTagListView calls loadMore() whenever the end of the list is on screen and no page is
  // loading. A failed page ends its loading with the end still on screen, so without a stop the view
  // requested the page again at once, forever (offline: a request and a snackbar per loop).
  @Test
  fun loadMore_afterAFailedPage_doesNotRequestItAgainUntilReload() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
    shopRepository.sendShop(testInputShop)
    itemTagRepository.sendItemTags(testInputItemTagsPage1)
    viewModel.reload()

    itemTagRepository.fail(ItemTagRepository::getItemTags, testApiError)
    viewModel.loadMore()
    assertEquals(TEST_API_ERROR_MESSAGE, viewModel.uiState.value.message)
    assertFalse(viewModel.uiState.value.isLoadingMore)

    // What the view does next: the end of the list is still visible and nothing is loading.
    viewModel.loadMore()
    // Page 1 by reload(), page 2 by the first loadMore(), nothing by the second.
    assertEquals(listOf<Int?>(1, 2), itemTagRepository.requestedPages)

    // Pull to refresh starts over, so loading more is allowed again.
    viewModel.reload()
    assertFalse(viewModel.uiState.value.loadMoreFailed)
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
    ),
  )

private var testInputShop = Shop(
  datum = testInputShopsData,
)

private const val ITEM_TAG_TYPE = "item_tag"
private const val ITEM_TAG_1_ID = "9712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val ITEM_TAG_2_ID = "9712F2DF-DFC7-A3AA-66BC-191203654A1B"
private const val ITEM_TAG_3_ID = "9712F2DF-DFC7-A3AA-66BC-191203654A1C"
private const val ITEM_TAG_1_NAME = "A001"
private const val ITEM_TAG_2_NAME = "A002"
private const val ITEM_TAG_3_NAME = "A003"
private const val ITEM_TAG_STATE = "idled"
private const val ITEM_TAG_CREATED_AT = "2025-01-02T12:00:00.000Z"
private const val ITEM_TAG_COMPLETED_AT = "2025-01-02T12:00:03.000Z"

private val testInputItemTagsData = listOf(
  Data(
    id = ITEM_TAG_1_ID,
    type = ITEM_TAG_TYPE,
    attributes = Attributes(
      shopId = SHOP_ID,
      name = ITEM_TAG_1_NAME,
      description = "",
      position = 1,
      state = ITEM_TAG_STATE,
      createdAt = ITEM_TAG_CREATED_AT,
      shopName = SHOP_NAME,
      completedAt = ITEM_TAG_COMPLETED_AT,
    ),
  ),
  Data(
    id = ITEM_TAG_2_ID,
    type = ITEM_TAG_TYPE,
    attributes = Attributes(
      shopId = SHOP_ID,
      name = ITEM_TAG_2_NAME,
      description = "",
      position = 2,
      state = ITEM_TAG_STATE,
      createdAt = ITEM_TAG_CREATED_AT,
      shopName = SHOP_NAME,
    ),
  ),
  Data(
    id = ITEM_TAG_3_ID,
    type = ITEM_TAG_TYPE,
    attributes = Attributes(
      shopId = SHOP_ID,
      name = ITEM_TAG_3_NAME,
      description = "",
      position = 3,
      state = ITEM_TAG_STATE,
      createdAt = ITEM_TAG_CREATED_AT,
      shopName = SHOP_NAME,
      completedAt = ITEM_TAG_COMPLETED_AT,
    ),
  ),
)

private val testInputItemTags = ItemTags(
  datum = testInputItemTagsData,
)

private val testInputItemTag = ItemTag(
  datum = testInputItemTagsData.first(),
)

private val testInputItemTagsPage1 = ItemTags(
  datum = listOf(testInputItemTagsData[0], testInputItemTagsData[1]),
  meta = Meta(currentPage = 1, totalPages = 2, totalCount = 3, limit = 2),
)

private val testInputItemTagsPage2 = ItemTags(
  datum = listOf(testInputItemTagsData[2]),
  meta = Meta(currentPage = 2, totalPages = 2, totalCount = 3, limit = 2),
)

private fun pagedItemTags(page: Int, totalPages: Int, perPage: Int): ItemTags = ItemTags(
  datum = (1..perPage).map { n ->
    val number = (page - 1) * perPage + n
    Data(
      id = "item-tag-$number",
      type = ITEM_TAG_TYPE,
      attributes = Attributes(
        shopId = SHOP_ID,
        name = "A%03d".format(number),
        description = "",
        position = number,
        state = ITEM_TAG_STATE,
        createdAt = ITEM_TAG_CREATED_AT,
      ),
    )
  },
  meta = Meta(currentPage = page, totalPages = totalPages, totalCount = totalPages * perPage, limit = perPage),
)
