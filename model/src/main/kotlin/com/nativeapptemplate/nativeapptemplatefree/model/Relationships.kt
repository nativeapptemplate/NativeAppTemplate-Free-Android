package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Relationships(
  val shop: Shop? = null,

  val shops: Shops? = null,
) : Parcelable
