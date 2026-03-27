package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class SendResetPassword(
  val email: String,

  @SerialName("redirect_url")
  val redirectUrl: String,
) : Parcelable {
  companion object {
    fun redirectUrlString(baseUrlString: String): String = "$baseUrlString/shopkeeper_auth/reset_password/edit"
  }
}
