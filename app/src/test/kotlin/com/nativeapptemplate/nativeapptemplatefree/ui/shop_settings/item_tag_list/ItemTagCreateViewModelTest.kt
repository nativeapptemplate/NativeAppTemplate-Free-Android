package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.nativeapptemplate.nativeapptemplatefree.NatConstants
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.util.MainDispatcherRule
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagCreateRoute
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
class ItemTagCreateViewModelTest {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val itemTagRepository = TestItemTagRepository()

  private lateinit var viewModel: ItemTagCreateViewModel

  @Before
  fun setUp() {
    viewModel = ItemTagCreateViewModel(
      savedStateHandle = SavedStateHandle(
        route = ItemTagCreateRoute(shopId = testInputShop.datum!!.id!!),
      ),
      itemTagRepository = itemTagRepository,
    )
  }

  @Test
  fun maximumNameLength_matchesConstant() = runTest {
    assertEquals(NatConstants.MAXIMUM_ITEM_TAG_NAME_LENGTH, viewModel.uiState.value.maximumNameLength)
  }

  @Test
  fun stateIsCreated_whenCreatingItemTag_becomesTrue() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    itemTagRepository.sendItemTag(testInputItemTag)

    val newName = "Buy milk"
    viewModel.updateName(newName)
    assertEquals(viewModel.uiState.value.name, newName)
    assertFalse(viewModel.hasInvalidData())

    viewModel.createItemTag()

    val uiStateValue = viewModel.uiState.value
    assertTrue(uiStateValue.isCreated)
  }

  @Test
  fun blankName_isInvalid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updateName("")

    assertTrue(viewModel.hasInvalidDataName())
    assertTrue(viewModel.hasInvalidData())
  }

  @Test
  fun whitespaceOnlyName_isInvalid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updateName("   ")

    assertTrue(viewModel.hasInvalidDataName())
    assertTrue(viewModel.hasInvalidData())
  }

  @Test
  fun singleCharacterName_isValid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updateName("A")

    assertFalse(viewModel.hasInvalidDataName())
  }

  @Test
  fun nameWithSymbolsAndUnicode_isValid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updateName("Buy milk 🥛 + bread")

    assertFalse(viewModel.hasInvalidDataName())
  }

  @Test
  fun nameAtMaximumLength_isValid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updateName("A".repeat(NatConstants.MAXIMUM_ITEM_TAG_NAME_LENGTH))

    assertFalse(viewModel.hasInvalidDataName())
  }

  @Test
  fun nameAboveMaximumLength_isRejectedByUpdater() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updateName("A".repeat(NatConstants.MAXIMUM_ITEM_TAG_NAME_LENGTH + 1))

    // updater clamps; value should remain blank (initial)
    assertEquals("", viewModel.uiState.value.name)
  }

  @Test
  fun descriptionAtMaximumLength_isValid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updateDescription("D".repeat(NatConstants.MAXIMUM_ITEM_TAG_DESCRIPTION_LENGTH))

    assertFalse(viewModel.hasInvalidDataDescription())
  }

  @Test
  fun descriptionAboveMaximumLength_isRejectedByUpdater() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updateDescription("D".repeat(NatConstants.MAXIMUM_ITEM_TAG_DESCRIPTION_LENGTH + 1))

    assertEquals("", viewModel.uiState.value.description)
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
private const val ITEM_TAG_ID = "9712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val ITEM_TAG_NAME = "A001"
private const val ITEM_TAG_STATE = "idled"
private const val ITEM_TAG_CREATED_AT = "2025-01-02T12:00:00.000Z"
private const val ITEM_TAG_COMPLETED_AT = "2025-01-02T12:00:03.000Z"

private val testInputItemTagsData =
  Data(
    id = ITEM_TAG_ID,
    type = ITEM_TAG_TYPE,
    attributes = Attributes(
      shopId = SHOP_ID,
      name = ITEM_TAG_NAME,
      description = "",
      position = 1,
      state = ITEM_TAG_STATE,
      createdAt = ITEM_TAG_CREATED_AT,
      shopName = SHOP_NAME,
      completedAt = ITEM_TAG_COMPLETED_AT,
    ),
  )

private val testInputItemTag = ItemTag(
  datum = testInputItemTagsData,
)
