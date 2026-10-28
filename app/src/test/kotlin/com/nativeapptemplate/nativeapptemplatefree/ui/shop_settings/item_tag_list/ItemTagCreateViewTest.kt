package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list

import android.content.Context
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import androidx.test.core.app.ApplicationProvider
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.common.MessageBus
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagCreateRoute
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ItemTagCreateViewTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  private val itemTagRepository = TestItemTagRepository()

  private val viewModel = ItemTagCreateViewModel(
    savedStateHandle = SavedStateHandle(route = ItemTagCreateRoute(shopId = "shop-1")),
    itemTagRepository = itemTagRepository,
    messageBus = MessageBus(),
  )

  // A new form opened with "Name is invalid." in red before anything was typed. The error waits
  // until the field is first edited (iOS's untilEdited); the save button stays disabled meanwhile.
  // Found on 2026-10-28 on a device.
  @Test
  fun nameError_waitsUntilTheNameIsEdited() {
    val nameInvalidMessage = ApplicationProvider.getApplicationContext<Context>()
      .getString(R.string.item_tag_name_is_invalid)
    composeTestRule.setContent {
      ItemTagCreateView(viewModel = viewModel, onShowSnackbar = { _, _, _ -> true }, onBackClick = {})
    }
    composeTestRule.waitForIdle()
    // Blank is invalid (ItemTagCreateViewModel.hasInvalidDataName), but nothing was typed yet
    composeTestRule.onNodeWithText(nameInvalidMessage, useUnmergedTree = true).assertDoesNotExist()

    composeTestRule.runOnIdle { viewModel.updateName("A") }
    composeTestRule.runOnIdle { viewModel.updateName("") }
    composeTestRule.onNodeWithText(nameInvalidMessage, useUnmergedTree = true).assertExists()
  }

  // The app shows the notice (MessageBus) and the screen closes at once, so the same tag cannot be
  // submitted again. Closing twice would pop the screen under it too.
  @Test
  fun created_closesTheScreenOnce_evenWhenTheScreenRecomposes() {
    var closedCount = 0
    composeTestRule.setContent {
      ItemTagCreateView(
        viewModel = viewModel,
        onShowSnackbar = { _, _, _ -> true },
        onBackClick = { closedCount++ },
      )
    }
    itemTagRepository.sendItemTag(createdItemTag)
    composeTestRule.runOnIdle {
      viewModel.updateName("A001")
      viewModel.createItemTag()
    }

    // Further state changes recompose the screen while isCreated is still true.
    composeTestRule.runOnIdle { viewModel.updateName("A002") }
    composeTestRule.runOnIdle { viewModel.updateName("A003") }
    composeTestRule.waitForIdle()

    assertEquals(1, closedCount)
  }
}

private val createdItemTag = ItemTag(
  datum = Data(
    id = "9712F2DF-DFC7-A3AA-66BC-191203654A1A",
    type = "item_tag",
    attributes = Attributes(
      shopId = "shop-1",
      name = "A001",
      description = "",
      position = 1,
      state = "idled",
      createdAt = "2025-01-02T12:00:00.000Z",
    ),
  ),
)
