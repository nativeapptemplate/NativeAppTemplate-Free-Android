package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ShopUpdateBodyDetail(
  val name: String,

  val description: String?,

  @SerialName("time_zone")
  val timeZone: String,
) : Parcelable
