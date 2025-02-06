package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Serializable
@Parcelize
data class Relationships(
  val shopkeepers: Shopkeepers? = null,

  val shop: Shop? = null,

  val shops: Shops? = null,
) : Parcelable
