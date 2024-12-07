package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

@Serializable
@Parcelize
data class UpdatePasswordBodyDetail(
  @SerialName("current_password")
  val currentPassword: String,

  val password: String,

  @SerialName("password_confirmation")
  val passwordConfirmation: String,
) : Parcelable
