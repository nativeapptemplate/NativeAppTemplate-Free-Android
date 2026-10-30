package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

/**
 * The app's Scaffold shows its snackbar over the screens, whose own Scaffolds do not know of it:
 * "Shop updated." covered most of the save button while it was shown.
 */
@RunWith(RobolectricTestRunner::class)
class AppSnackbarTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun aShownSnackbar_doesNotCoverTheScreensFloatingButton() {
    val hostState = SnackbarHostState()
    // As NativeAppTemplateApp: the app's Scaffold, and a screen's Scaffold in its content
    composeTestRule.setContent {
      var snackbarHeight by remember { mutableStateOf(0.dp) }
      Scaffold(
        snackbarHost = { AppSnackbarHost(hostState, onHeightChange = { snackbarHeight = it }) },
      ) { padding ->
        CompositionLocalProvider(LocalAppSnackbarHeight provides snackbarHeight) {
          Scaffold(
            modifier = Modifier.padding(padding),
            floatingActionButton = { SubmitFab(label = SAVE, onClick = {}) },
          ) { innerPadding -> Box(Modifier.padding(innerPadding)) }
        }
      }
      LaunchedEffect(Unit) { hostState.showSnackbar(MESSAGE, duration = SnackbarDuration.Indefinite) }
    }
    composeTestRule.waitForIdle()

    val button = composeTestRule.onNodeWithContentDescription(SAVE).getBoundsInRoot()
    val message = composeTestRule.onNodeWithText(MESSAGE).getBoundsInRoot()
    assertTrue("The button ($button) is under the snackbar's text ($message)", button.bottom <= message.top)
  }

  @Test
  fun everyScreensFloatingButton_staysAboveTheSnackbar() {
    val screens = File("src/main/kotlin/com/nativeapptemplate/nativeapptemplatefree/ui").walkTopDown()
      .filter { it.isFile && it.extension == "kt" }
      .filter { "floatingActionButton = {" in it.readText() }
      .toList()
    assertTrue("Found ${screens.size} screens", screens.size >= 11) // guards against the wrong folder
    val offenders = screens
      .filterNot { it.readText().let { text -> "SubmitFab(" in text || "aboveAppSnackbar()" in text } }
      .map { it.name }
    assertTrue("Floating buttons without aboveAppSnackbar: $offenders", offenders.isEmpty())
  }

  private companion object {
    const val SAVE = "Save"
    const val MESSAGE = "Shop updated."
  }
}
