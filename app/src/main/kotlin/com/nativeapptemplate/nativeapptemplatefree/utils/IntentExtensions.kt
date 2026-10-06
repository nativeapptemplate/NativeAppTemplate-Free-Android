package com.nativeapptemplate.nativeapptemplatefree.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.nativeapptemplate.nativeapptemplatefree.R

/**
 * Opens the first of [uris] that an installed app can handle (e.g. the Play Store app, then the
 * Play Store website). When none can (no browser, a restricted work profile), shows a short
 * message and returns false instead of crashing with [ActivityNotFoundException].
 */
fun Context.openUri(vararg uris: Uri): Boolean {
  for (uri in uris) {
    try {
      startActivity(Intent(Intent.ACTION_VIEW, uri))
      return true
    } catch (_: ActivityNotFoundException) {
      // Try the next one.
    }
  }
  Toast.makeText(this, R.string.message_no_app_to_open_link, Toast.LENGTH_SHORT).show()
  return false
}
