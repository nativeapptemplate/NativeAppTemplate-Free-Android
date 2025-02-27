package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

@Serializable
@Parcelize
data class ShopUpdateBody(
  @SerialName("shop")
  val shopUpdateBodyDetail: ShopUpdateBodyDetail
) : Parcelable

