package com.nativeapptemplate.nativeapptemplatefree

import android.app.Application
import com.nativeapptemplate.nativeapptemplatefree.common.errors.ErrorMessages
import com.nativeapptemplate.nativeapptemplatefree.utils.ProfileVerifierLogger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NativeAppTemplateApplication : Application() {
  @Inject
  lateinit var profileVerifierLogger: ProfileVerifierLogger

  override fun onCreate() {
    // Before anything that may make an error (super.onCreate() runs Hilt's injection).
    ErrorMessages.install(this)
    super.onCreate()
    profileVerifierLogger()
  }
}
