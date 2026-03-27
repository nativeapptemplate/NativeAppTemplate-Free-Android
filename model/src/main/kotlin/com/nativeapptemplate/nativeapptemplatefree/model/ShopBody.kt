package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ShopBody(
  @SerialName("shop")
  val shopBodyDetail: ShopBodyDetail,
) : Parcelable
