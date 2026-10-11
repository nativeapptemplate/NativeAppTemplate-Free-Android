package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class SignUpForUpdate(
  val name: String,

  val email: String,

  @SerialName("time_zone")
  val timeZone: String,

  // "en" or "ja" (Locales.supported). No default, so it is always in the payload.
  val locale: String,
) : Parcelable
