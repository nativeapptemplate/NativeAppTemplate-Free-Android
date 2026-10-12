package com.nativeapptemplate.nativeapptemplatefree.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.nativeapptemplate.nativeapptemplatefree.UserPreferences
import com.nativeapptemplate.nativeapptemplatefree.model.DarkThemeConfig
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import java.io.IOException

/** A failed DataStore write is logged and rethrown to the caller. Robolectric for [ShadowLog]. */
@RunWith(RobolectricTestRunner::class)
class NativeAppTemplatePreferencesDataSourceWriteFailureTest {
  private val writeError = IOException("disk full")

  private val subject = NativeAppTemplatePreferencesDataSource(
    object : DataStore<UserPreferences> {
      override val data = MutableStateFlow(UserPreferences.getDefaultInstance())
      override suspend fun updateData(transform: suspend (t: UserPreferences) -> UserPreferences): UserPreferences = throw writeError
    },
  )

  private suspend fun assertLoggedAndRethrown(expectedLog: String, write: suspend () -> Unit) {
    ShadowLog.clear()

    val thrown = runCatching { write() }.exceptionOrNull()

    assertSame(writeError, thrown)
    val log = ShadowLog.getLogsForTag("NativeAppTemplatePreferences").single()
    assertEquals(Log.ERROR, log.type)
    assertEquals(expectedLog, log.msg)
    assertSame(writeError, log.throwable)
  }

  @Test
  fun aSetting_whenTheWriteFails_isLoggedAndRethrown() = runTest {
    assertLoggedAndRethrown("Failed to update user preferences") { subject.setDarkThemeConfig(DarkThemeConfig.DARK) }
  }

  @Test
  fun permissions_whenTheWriteFails_areLoggedAndRethrown() = runTest {
    assertLoggedAndRethrown("Failed to update user preferences") { subject.setPermissions(Permissions()) }
  }

  @Test
  fun aFlag_whenTheWriteFails_isLoggedAndRethrown() = runTest {
    assertLoggedAndRethrown("Failed to update user preferences") { subject.setIsShopDeleted(true) }
  }

  @Test
  fun clearing_whenTheWriteFails_isLoggedAndRethrown() = runTest {
    assertLoggedAndRethrown("Failed to clear user preferences") { subject.clearUserPreferences() }
  }
}
