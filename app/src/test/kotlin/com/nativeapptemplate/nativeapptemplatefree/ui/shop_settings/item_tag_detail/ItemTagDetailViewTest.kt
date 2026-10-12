package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.invoke
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagDetailRoute
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The detail screen sits under the edit screen, so it must show the saved values on return. */
@RunWith(RobolectricTestRunner::class)
class ItemTagDetailViewTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  private val itemTagRepository = TestItemTagRepository()
  private val viewModel = ItemTagDetailViewModel(
    savedStateHandle = SavedStateHandle(route = ItemTagDetailRoute(id = ITEM_TAG_ID)),
    itemTagRepository = itemTagRepository,
  )

  @Test
  fun returningFromTheEditScreen_reloadsTheItemTag() {
    itemTagRepository.sendItemTag(itemTag)
    lateinit var navController: NavHostController
    composeTestRule.setContent {
      navController = rememberNavController()
      NavHost(navController = navController, startDestination = DETAIL) {
        composable(DETAIL) {
          ItemTagDetailView(
            viewModel = viewModel,
            onShowItemTagEditClick = {},
            onShowSnackbar = { _, _, _ -> true },
            onBackClick = {},
          )
        }
        // Stands in for ItemTagEditView, where the item tag is saved.
        composable(EDIT) { Text("Edit") }
      }
    }
    composeTestRule.waitForIdle()
    assertEquals(1, itemTagRepository.getItemTagCallCount)

    composeTestRule.runOnIdle { navController.navigate(EDIT) }
    composeTestRule.waitForIdle()
    composeTestRule.runOnIdle { navController.popBackStack() }
    composeTestRule.waitForIdle()

    // One load on the way in, one on the way back from the edit screen.
    assertEquals(2, itemTagRepository.getItemTagCallCount)
  }
}

private const val DETAIL = "detail"
private const val EDIT = "edit"
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
