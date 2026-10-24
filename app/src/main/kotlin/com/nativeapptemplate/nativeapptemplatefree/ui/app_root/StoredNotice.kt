package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import com.nativeapptemplate.nativeapptemplatefree.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Notices stored as flags in DataStore by the screen that caused them. Each is cleared as soon as it
// is taken for display, not after the snackbar is dismissed: an Indefinite snackbar returns only
// when dismissed, and the app can be closed while it is up, which would leave the flag set and show
// the notice again at the next launch. Not keyed on the flag in a LaunchedEffect, so that clearing
// the flag does not cancel the snackbar.

/**
 * Shows the notice once each time [flag] turns true while the app runs: for notices stored when the
 * shopkeeper is signed out (email changed, account deleted), which the same process shows.
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
 * Shows the notice if [flag] is set when the app starts: for notices stored just before the app
 * restarts itself (shop deleted). The process that stored the
 * flag must not take it: its snackbar would be gone a moment later with the process, and the
 * restarted app would find the flag cleared.
 *
 * These notices are about the session, so signing out ([isLoggedIn] turning false) closes one that
 * is still up: cancelling [show] dismisses its snackbar, which otherwise stayed over the sign-in
 * screen.
 */
internal suspend fun showNoticeStoredForLaunch(
  flag: Flow<Boolean>,
  clear: suspend () -> Unit,
  isLoggedIn: Flow<Boolean>,
  show: suspend () -> Unit,
) {
  if (flag.first()) {
    clear()
    coroutineScope {
      val notice = launch { show() }
      val signOut = launch {
        isLoggedIn.first { !it }
        notice.cancel()
      }
      notice.join()
      signOut.cancel()
    }
  }
}

/**
 * How long a stored notice stays. A confirmation of something done (shop or account deleted) goes
 * by itself: kept until dismissed, it covered the add button. The new address's confirmation mail
 * asks the shopkeeper to act, so that one stays until dismissed.
 */
internal fun storedNoticeDuration(@StringRes messageId: Int): SnackbarDuration = when (messageId) {
  R.string.reconfirm_description -> SnackbarDuration.Indefinite
  else -> SnackbarDuration.Short
}
