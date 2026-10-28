package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.v2.createComposeRule
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** iOS's untilEdited(_:): a field's error waits until the field is first edited. */
@RunWith(RobolectricTestRunner::class)
class IsEditedTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun becomesEditedOnTheFirstChange_andStaysEdited() {
    var text by mutableStateOf("")
    var isEdited = false
    composeTestRule.setContent {
      isEdited = rememberIsEdited(text)
    }

    composeTestRule.waitForIdle()
    assertFalse(isEdited)

    composeTestRule.runOnIdle { text = "A" }
    composeTestRule.waitForIdle()
    assertTrue(isEdited)

    // Cleared again: still edited, so the "required" error shows
    composeTestRule.runOnIdle { text = "" }
    composeTestRule.waitForIdle()
    assertTrue(isEdited)
  }
}
