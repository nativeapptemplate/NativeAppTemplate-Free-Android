package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.testing.invoke
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagCreateRoute
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ItemTagCreateViewTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  private val itemTagRepository = TestItemTagRepository()

  private val viewModel = ItemTagCreateViewModel(
    savedStateHandle = SavedStateHandle(route = ItemTagCreateRoute(shopId = "shop-1")),
    itemTagRepository = itemTagRepository,
  )

  private val lifecycleOwner = object : LifecycleOwner {
    val registry = LifecycleRegistry.createUnsafe(this).apply { currentState = Lifecycle.State.RESUMED }
    override val lifecycle: Lifecycle get() = registry
  }

  private val createdMessage = RuntimeEnvironment.getApplication().getString(R.string.message_item_tag_created)

  @Test
  fun createdDialog_staysAfterTheAppIsBackgroundedAndResumed() {
    composeTestRule.setContent {
      CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
        ItemTagCreateView(
          viewModel = viewModel,
          onShowSnackbar = { _, _, _ -> true },
          onBackClick = {},
        )
      }
    }
    itemTagRepository.sendItemTag(createdItemTag)
    composeTestRule.runOnIdle {
      viewModel.updateName("A001")
      viewModel.createItemTag()
    }
    composeTestRule.onNodeWithText(createdMessage).assertIsDisplayed()

    // The user switches to another app and comes back.
    composeTestRule.runOnIdle {
      lifecycleOwner.registry.currentState = Lifecycle.State.STARTED
      lifecycleOwner.registry.currentState = Lifecycle.State.RESUMED
    }

    // A reset on resume would hide the dialog and let the user create the same tag again.
    composeTestRule.onNodeWithText(createdMessage).assertIsDisplayed()
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
