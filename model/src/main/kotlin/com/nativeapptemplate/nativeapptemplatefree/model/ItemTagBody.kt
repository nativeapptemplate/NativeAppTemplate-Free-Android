package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

@Serializable
@Parcelize
data class ItemTagBody(
  @SerialName("item_tag")
  var itemTagBodyDetail: ItemTagBodyDetail
) : Parcelable

