package com.nativeapptemplate.nativeapptemplatefree.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import com.nativeapptemplate.nativeapptemplatefree.BuildConfig
import com.nativeapptemplate.nativeapptemplatefree.NatConstants
import com.nativeapptemplate.nativeapptemplatefree.R
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

object Utility {
  fun String.validateEmail(): Boolean {
    return this.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
  }

  fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
  }

  fun isAlphanumeric(text: String?): Boolean {
    if (text.isNullOrBlank()) return false

    return text.matches("^[a-zA-Z0-9]*$".toRegex())
  }

  fun marketUri(): Uri {
    val appId = BuildConfig.APPLICATION_ID
    return Uri.parse("market://details?id=$appId")
  }

  fun googlePlayStoreUri(): Uri {
    val appId = BuildConfig.APPLICATION_ID
    return Uri.parse("https://play.google.com/store/apps/details?id=$appId")
  }

  // https://stackoverflow.com/a/75714502/1160200
  // https://qiita.com/irgaly/items/b942bd985a4647e372ea
  fun Context.shareImage(title: String, image: ImageBitmap, filename: String) {
    val file = File(cacheDir, "$filename.png").also { outputFile ->
      FileOutputStream(outputFile).use { outputStream ->
        image.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, outputStream)
      }
    }

    val uri = file.toUriCompat(this)

    val shareIntent = Intent().apply {
      action = Intent.ACTION_SEND
      type = "image/png"
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      putExtra(Intent.EXTRA_STREAM, uri)
    }
    startActivity(Intent.createChooser(shareIntent, title))
  }

  private fun File.toUriCompat(context: Context): Uri {
    return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", this)
  }

  // https://stackoverflow.com/a/78039163/1160200
  fun Context.restartApp() {
    val packageManager = packageManager
    val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return
    val componentName = intent.component ?: return
    val restartIntent = Intent.makeRestartActivityTask(componentName)
    startActivity(restartIntent)
    Runtime.getRuntime().exit(0)
  }

  fun sendContactMail(ctx: Context) {
    val appName = ctx.getString(R.string.app_name)
    val systemVersion = Build.VERSION.RELEASE
    val device = Build.MODEL
    val appVersion = BuildConfig.VERSION_NAME
    val language = Locale.getDefault().language
    val body = "\n\n\n-----\n$appName $appVersion\n$device ($systemVersion)\n$language"

    val emailIntent = Intent(Intent.ACTION_SENDTO)

    val uriText = "mailto:" + Uri.encode(NatConstants.SUPPORT_MAIL) +
      "?subject=" + Uri.encode("$appName for Android support") +
      "&body=" + Uri.encode(body)

    emailIntent.setData(Uri.parse(uriText))
    ctx.startActivity(emailIntent)
  }
}
