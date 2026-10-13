package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

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
    InMemoryDataStore(UserPreferences.getDefaultInstance()),
  )

  private var shopDeletedShownCount = 0
  private var emailUpdatedShownCount = 0

  /** An Indefinite snackbar: showSnackbar returns only when the shopkeeper dismisses it. */
  private suspend fun snackbar(isDismissed: Boolean) {
    if (!isDismissed) awaitCancellation()
  }

  /** One app process, showing the notices as NativeAppTemplateApp does; cancelled = process ended. */
  private fun CoroutineScope.launchApp(isDismissed: Boolean = false) = launch {
    launch {
      showNoticeStoredForLaunch(
        flag = preferences.isShopDeleted(),
        clear = { preferences.setIsShopDeleted(false) },
      ) {
        shopDeletedShownCount++
        snackbar(isDismissed)
      }
    }
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

  // Regression: the flag was cleared only after the snackbar was dismissed, so closing the app while
  // it showed made it show again at every launch.
  @Test
  fun shopDeleted_isShownOnceByTheRestartedApp_evenIfItIsClosedWhileTheNoticeShows() = runTest(UnconfinedTestDispatcher()) {
    val beforeTheDeletion = launchApp()
    // ShopSettingsViewModel.deleteShop stores the flag, then the app restarts itself.
    preferences.setIsShopDeleted(true)
    beforeTheDeletion.cancel()
    // Not by the process that is about to exit, whose snackbar would vanish with it.
    assertEquals(0, shopDeletedShownCount)

    // The restarted app shows it, and is closed while the snackbar is still up.
    launchApp().cancel()
    launchApp().cancel()

    // Shown by the first launch after the deletion only.
    assertEquals(1, shopDeletedShownCount)
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
}
