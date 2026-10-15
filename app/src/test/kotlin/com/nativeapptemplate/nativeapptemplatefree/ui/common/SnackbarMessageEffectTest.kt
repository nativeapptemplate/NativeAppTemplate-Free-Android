package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.v2.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SnackbarMessageEffectTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  // A view model clears its message when a new attempt starts (ErrorShowingUiState). That must
  // close the last attempt's snackbar, which stays until dismissed (Indefinite), and a failure of
  // the new attempt must show again, even with the same text.
  @Test
  fun clearingTheMessage_closesItsSnackbar_andTheNextErrorShowsAgain() {
    val host = SnackbarHostState()
    var message by mutableStateOf("")
    composeTestRule.setContent {
      SnackbarMessageEffect(
        message = message,
        // As the app root passes it to the screens.
        onShowSnackbar = { text, action, duration ->
          host.showSnackbar(text, action, duration = duration) == SnackbarResult.ActionPerformed
        },
        onMessageShown = { message = "" },
      )
    }

    composeTestRule.runOnIdle { message = OFFLINE_ERROR }
    composeTestRule.waitForIdle()
    assertEquals(OFFLINE_ERROR, host.currentSnackbarData?.visuals?.message)

    // Reload starts.
    composeTestRule.runOnIdle { message = "" }
    composeTestRule.waitForIdle()
    assertNull(host.currentSnackbarData)

    // Reload fails again.
    composeTestRule.runOnIdle { message = OFFLINE_ERROR }
    composeTestRule.waitForIdle()
    assertEquals(OFFLINE_ERROR, host.currentSnackbarData?.visuals?.message)
  }
}

private const val OFFLINE_ERROR = "[NATIVEAPPTEMPLATE-2002] Something went wrong while communicating with the server. Please try again later."
