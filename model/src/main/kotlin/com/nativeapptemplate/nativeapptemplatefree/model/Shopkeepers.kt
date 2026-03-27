package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Shopkeepers(
  @SerialName("data")
  val datum: List<Data> = emptyList(),
  val included: List<Data>? = null,
  val meta: Meta? = null,
) : Parcelable
