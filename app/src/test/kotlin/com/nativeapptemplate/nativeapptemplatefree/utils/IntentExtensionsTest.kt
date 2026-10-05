package com.nativeapptemplate.nativeapptemplatefree.utils

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
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
    val viewer = ComponentName("com.example.viewer", "com.example.viewer.Viewer")
    val packageManager = shadowOf(application.packageManager)
    packageManager.addActivityIfNotPresent(viewer)
    packageManager.addIntentFilterForActivity(
      viewer,
      IntentFilter(Intent.ACTION_VIEW).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        addDataScheme(uri.scheme)
        addDataAuthority(uri.host, null)
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
