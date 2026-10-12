package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail

import android.content.Context
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import androidx.test.core.app.ApplicationProvider
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagEditRoute
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The "is invalid" messages under the fields follow the input as it is edited. */
@RunWith(RobolectricTestRunner::class)
class ItemTagEditViewTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  private val itemTagRepository = TestItemTagRepository()
  private val viewModel = ItemTagEditViewModel(
    savedStateHandle = SavedStateHandle(route = ItemTagEditRoute(id = ITEM_TAG_ID)),
    itemTagRepository = itemTagRepository,
  )

  private val context = ApplicationProvider.getApplicationContext<Context>()
  private val nameInvalidMessage = context.getString(R.string.item_tag_name_is_invalid)
  private val descriptionInvalidMessage = context.getString(R.string.item_tag_description_is_invalid)

  private fun setContent() {
    itemTagRepository.sendItemTag(itemTag)
    composeTestRule.setContent {
      ItemTagEditView(viewModel = viewModel, onShowSnackbar = { _, _, _ -> true }, onBackClick = {})
    }
    composeTestRule.waitForIdle()
  }

  private fun nameInvalidNode() = composeTestRule.onNodeWithText(nameInvalidMessage, useUnmergedTree = true)

  @Test
  fun nameInvalidMessage_goesAway_onceTheNameIsValid() {
    setContent()
    // The loaded name "A001" is valid (not blank, 4 <= MAXIMUM_ITEM_TAG_NAME_LENGTH).
    nameInvalidNode().assertDoesNotExist()

    // A blank name is invalid (ItemTagEditViewModel.hasInvalidDataName).
    composeTestRule.runOnIdle { viewModel.updateName("") }
    nameInvalidNode().assertExists()

    composeTestRule.runOnIdle { viewModel.updateName("A002") }
    // The message must leave the screen (and the accessibility tree), not stay behind.
    nameInvalidNode().assertDoesNotExist()
  }

  @Test
  fun descriptionInvalidMessage_isNotShown_forAValidDescription() {
    setContent()
    // "" and "Front desk" are within MAXIMUM_ITEM_TAG_DESCRIPTION_LENGTH, so both are valid.
    composeTestRule.onNodeWithText(descriptionInvalidMessage, useUnmergedTree = true).assertDoesNotExist()
    composeTestRule.runOnIdle { viewModel.updateDescription("Front desk") }
    composeTestRule.onNodeWithText(descriptionInvalidMessage, useUnmergedTree = true).assertDoesNotExist()
  }
}

private const val ITEM_TAG_ID = "9712F2DF-DFC7-A3AA-66BC-191203654A1A"

private val itemTag = ItemTag(
  datum = Data(
    id = ITEM_TAG_ID,
    type = "item_tag",
    attributes = Attributes(
      shopId = "5712F2DF-DFC7-A3AA-66BC-191203654A1A",
      name = "A001",
      description = "",
      position = 1,
      state = "idled",
      createdAt = "2025-01-02T12:00:00.000Z",
      shopName = "8th & Townsend",
    ),
  ),
)
