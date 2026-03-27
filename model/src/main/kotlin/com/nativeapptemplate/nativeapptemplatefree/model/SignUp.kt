package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class SignUp(
  val name: String,

  val email: String,

  @SerialName("time_zone")
  val timeZone: String,

  val password: String,

  @SerialName("current_platform")
  val currentPlatform: String,
) : Parcelable
