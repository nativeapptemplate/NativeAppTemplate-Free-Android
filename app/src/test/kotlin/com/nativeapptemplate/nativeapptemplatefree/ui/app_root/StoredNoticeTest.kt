package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import androidx.compose.material3.SnackbarDuration
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.UserPreferences
import com.nativeapptemplate.nativeapptemplatefree.datastore.NativeAppTemplatePreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.datastoreTest.InMemoryDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class StoredNoticeTest {
  // Outlives the "launches" below, as DataStore's file outlives the app process.
  private val preferences = NativeAppTemplatePreferencesDataSource(
    InMemoryDataStore(UserPreferences.getDefaultInstance().toBuilder().setIsLoggedIn(true).build()),
  )

  private var emailUpdatedShownCount = 0

  /** An Indefinite snackbar: showSnackbar returns only when the shopkeeper dismisses it. */
  private suspend fun snackbar(isDismissed: Boolean) {
    if (!isDismissed) awaitCancellation()
  }

  /** One app process, showing the notices as NativeAppTemplateApp does; cancelled = process ended. */
  private fun CoroutineScope.launchApp(isDismissed: Boolean = false) = launch {
    launch {
      showStoredNotice(
        flag = preferences.isEmailUpdated(),
        clear = { preferences.setIsEmailUpdated(false) },
      ) {
        emailUpdatedShownCount++
        snackbar(isDismissed)
      }
    }
  }

  @Test
  fun emailUpdated_isShownOnceByTheRunningApp_evenIfItIsClosedWhileTheNoticeShows() = runTest(UnconfinedTestDispatcher()) {
    val app = launchApp()
    // Changing the email signs out and stores the flag; the app does not restart.
    preferences.setIsEmailUpdated(true)
    assertEquals(1, emailUpdatedShownCount)

    // Closed while the snackbar is still up.
    app.cancel()
    launchApp().cancel()

    assertEquals(1, emailUpdatedShownCount)
  }

  @Test
  fun emailUpdated_storedAgain_isShownAgain() = runTest(UnconfinedTestDispatcher()) {
    val app = launchApp(isDismissed = true)

    // Two sign-outs in the same process, each notice dismissed.
    preferences.setIsEmailUpdated(true)
    preferences.setIsEmailUpdated(true)

    assertEquals(2, emailUpdatedShownCount)
    app.cancel()
  }

  @Test
  fun doneNotices_goByThemselves_andTheReconfirmationStays() {
    // A notice of something done stayed until dismissed and covered the add button: it goes by
    // itself. The new address's confirmation mail asks the shopkeeper to act, so it stays.
    assertEquals(SnackbarDuration.Short, storedNoticeDuration(R.string.message_shopkeeper_deleted))
    assertEquals(SnackbarDuration.Indefinite, storedNoticeDuration(R.string.reconfirm_description))
  }
}
