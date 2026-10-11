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

  // The device's language tag (e.g. "ja-JP"); the server reduces it to a supported language.
  // No default: with the Json config's encodeDefaults = false, a value equal to its default is
  // omitted from the payload.
  val locale: String,
) : Parcelable
