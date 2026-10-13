package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The swipe-to-reveal rows of the shop detail and item tag list screens: the row itself holds
 * whether its actions are revealed, so a swiped row stays open while the screen recomposes (e.g.
 * the list reloads) and closes when swiped back.
 */
@RunWith(RobolectricTestRunner::class)
class SwipeableItemWithActionsTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  private var recompositions by mutableIntStateOf(0)

  private fun setContent() {
    composeTestRule.setContent {
      Column {
        // Read here so changing it recomposes the parent of the row, as a list update does.
        Text("$recompositions")
        SwipeableItemWithActions(
          actions = { Box(Modifier.width(ACTIONS_WIDTH).height(48.dp)) },
        ) {
          Box(Modifier.fillMaxWidth().height(48.dp).testTag(ROW))
        }
      }
    }
  }

  @Test
  fun swipeLeft_revealsTheActions_andTheRowStaysOpenAcrossARecomposition() {
    setContent()

    composeTestRule.onNodeWithTag(ROW).performTouchInput { swipeLeft() }
    composeTestRule.waitForIdle()
    // Fully open = the row moved left by the width of the actions.
    composeTestRule.onNodeWithTag(ROW).assertLeftPositionInRootIsEqualTo(-ACTIONS_WIDTH)

    recompositions++
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(ROW).assertLeftPositionInRootIsEqualTo(-ACTIONS_WIDTH)
  }

  @Test
  fun swipeRight_onAnOpenRow_closesIt() {
    setContent()
    composeTestRule.onNodeWithTag(ROW).performTouchInput { swipeLeft() }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(ROW).performTouchInput { swipeRight() }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(ROW).assertLeftPositionInRootIsEqualTo(0.dp)
  }

  private companion object {
    const val ROW = "row"
    val ACTIONS_WIDTH = 96.dp
  }
}
