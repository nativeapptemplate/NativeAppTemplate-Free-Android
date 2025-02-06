package com.nativeapptemplate.nativeapptemplatefree.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.nativeapptemplate.nativeapptemplatefree.BuildConfig
import com.nativeapptemplate.nativeapptemplatefree.NatConstants
import com.nativeapptemplate.nativeapptemplatefree.R
import java.util.Locale

object Utility {
  fun String.validateEmail(): Boolean {
    return this.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
  }

  fun marketUri(): Uri {
    val appId = BuildConfig.APPLICATION_ID
    return Uri.parse("market://details?id=$appId")
  }

  fun googlePlayStoreUri(): Uri {
    val appId = BuildConfig.APPLICATION_ID
    return Uri.parse("https://play.google.com/store/apps/details?id=$appId")
  }

  // https://stackoverflow.com/a/78039163/1160200
  fun Context.restartApp() {
    val packageManager = packageManager
    val intent = packageManager.getLaunchIntentForPackage(packageName)!!
    val componentName = intent.component!!
    val restartIntent = Intent.makeRestartActivityTask(componentName)
    startActivity(restartIntent)
    Runtime.getRuntime().exit(0)
  }

  fun sendContactMail(ctx: Context) {
    val appName = ctx.getString(R.string.app_name)
    val systemVersion = Build.VERSION.RELEASE
    val device = Build.MODEL
    val appVersion= BuildConfig.VERSION_NAME
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