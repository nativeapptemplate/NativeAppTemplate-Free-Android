package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class ShowScreenMessageTest {
  // Found on 2026-10-29 on a device: a shop opened offline failed, and its "[NATIVEAPPTEMPLATE-2002]"
  // waited behind the offline snackbar, then came up once the connection was back
  @Test
  fun offline_dropsTheMessage() = runTest {
    val snackbarHostState = SnackbarHostState()

    val isActionPerformed = snackbarHostState.showScreenMessage(
      isOffline = true,
      message = "[NATIVEAPPTEMPLATE-2002] Failed to connect",
      actionLabel = "Dismiss",
      duration = SnackbarDuration.Indefinite,
    )

    assertFalse(isActionPerformed)
    assertNull(snackbarHostState.currentSnackbarData)
  }

  @Test
  fun online_showsTheMessage() = runTest {
    val snackbarHostState = SnackbarHostState()

    val shown = launch {
      snackbarHostState.showScreenMessage(
        isOffline = false,
        message = "Shop deleted",
        actionLabel = "Dismiss",
        duration = SnackbarDuration.Indefinite,
      )
    }
    testScheduler.runCurrent()

    assertEquals("Shop deleted", snackbarHostState.currentSnackbarData?.visuals?.message)
    shown.cancel()
  }
}
