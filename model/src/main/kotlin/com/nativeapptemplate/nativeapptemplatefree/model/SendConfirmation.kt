package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

@Serializable
@Parcelize
data class SendConfirmation(
  val email: String,

  @SerialName("redirect_url")
  val redirectUrl: String
) : Parcelable {
  companion object {
    fun redirectUrlString(baseUrlString: String): String = "${baseUrlString}/shopkeeper_auth/confirmation_result"
  }
}
