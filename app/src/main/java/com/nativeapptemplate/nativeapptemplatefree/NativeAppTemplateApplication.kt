package com.nativeapptemplate.nativeapptemplatefree

import android.app.Application
import com.nativeapptemplate.nativeapptemplatefree.utils.ProfileVerifierLogger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NativeAppTemplateApplication : Application() {
  @Inject
  lateinit var profileVerifierLogger: ProfileVerifierLogger

  override fun onCreate() {
    super.onCreate()
    profileVerifierLogger()
  }
}