package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

@Serializable
@Parcelize
data class Shopkeepers(
  @SerialName("data")
  val datum: List<Data> = emptyList(),
  val included: List<Data>? = null,
  val meta: Meta? = null
) : Parcelable
