package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.nativeapptemplate.nativeapptemplatefree.R

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
  val dismissLabel = stringResource(R.string.dismiss)
  LaunchedEffect(message) {
    if (message.isNotBlank()) {
      onShowSnackbar(message, dismissLabel, SnackbarDuration.Indefinite)
      onMessageShown()
    }
  }
}
