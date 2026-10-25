package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import com.nativeapptemplate.nativeapptemplatefree.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

// Notices stored as flags in DataStore by the screen that caused them. Each is cleared as soon as it
// is taken for display, not after the snackbar is dismissed: an Indefinite snackbar returns only
// when dismissed, and the app can be closed while it is up, which would leave the flag set and show
// the notice again at the next launch. Not keyed on the flag in a LaunchedEffect, so that clearing
// the flag does not cancel the snackbar.

/**
 * Shows the notice once each time [flag] turns true while the app runs: for notices stored when the
 * shopkeeper is signed out (email changed, account deleted), which the same process shows. A notice
 * from a screen that is still signed in goes through MessageBus instead.
 */
internal suspend fun showStoredNotice(
  flag: Flow<Boolean>,
  clear: suspend () -> Unit,
  show: suspend () -> Unit,
) {
  flag.filter { it }.collect {
    clear()
    show()
  }
}

/**
 * How long a stored notice stays. A confirmation of something done (the account deleted) goes by
 * itself: kept until dismissed, it covered the add button. The new address's confirmation mail asks
 * the shopkeeper to act, so that one stays until dismissed.
 */
internal fun storedNoticeDuration(@StringRes messageId: Int): SnackbarDuration = when (messageId) {
  R.string.reconfirm_description -> SnackbarDuration.Indefinite
  else -> SnackbarDuration.Short
}
