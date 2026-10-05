package com.nativeapptemplate.nativeapptemplatefree.utils

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.activity.ComponentActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class IntentExtensionsTest {
  private val application = RuntimeEnvironment.getApplication()
  private val shadowApplication = shadowOf(application)

  // The app calls openUri from an Activity context (LocalContext in a composable).
  private val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()

  // Make startActivity throw ActivityNotFoundException for intents nothing resolves, like a device.
  init {
    shadowApplication.checkActivities(true)
  }

  private fun installAppFor(uri: Uri) {
    shadowOf(application.packageManager).addResolveInfoForIntent(
      Intent(Intent.ACTION_VIEW, uri),
      ResolveInfo().apply {
        activityInfo = ActivityInfo().apply {
          packageName = "com.example.viewer"
          name = "Viewer"
        }
      },
    )
  }

  @Test
  fun openUri_whenNoAppCanOpenIt_returnsFalseInsteadOfCrashing() {
    // e.g. no browser on the device, or in a restricted work profile.
    assertFalse(activity.openUri(Uri.parse("https://example.com/faqs")))
  }

  @Test
  fun openUri_opensTheFirstUriThatAnAppCanHandle() {
    val marketUri = Uri.parse("market://details?id=com.example")
    val playStoreUri = Uri.parse("https://play.google.com/store/apps/details?id=com.example")
    installAppFor(playStoreUri)

    assertTrue(activity.openUri(marketUri, playStoreUri))
    assertEquals(playStoreUri, shadowApplication.nextStartedActivity.data)
  }
}
