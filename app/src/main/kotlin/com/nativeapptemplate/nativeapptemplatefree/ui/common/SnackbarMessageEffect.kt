package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

/**
 * Composable that displays a snackbar when [message] is non-blank,
 * then notifies the caller so it can clear the message from state.
 */
@Composable
fun SnackbarMessageEffect(
  message: String,
  onShowSnackbar: suspend (String, String?, SnackbarDuration) -> Boolean,
  onMessageShown: () -> Unit,
) {
  LaunchedEffect(message) {
    if (message.isNotBlank()) {
      onShowSnackbar(message, "dismiss", SnackbarDuration.Indefinite)
      onMessageShown()
    }
  }
}
