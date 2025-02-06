package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

@Serializable
@Parcelize
data class Shopkeeper(
  @SerialName("data")
  var datum: Data? = null,
  val meta: Meta? = null,
  val included: List<Data>? = null
) : Parcelable {
  fun getData(): Data? {
    if (included.isNullOrEmpty()) {
      return datum
    }

    return datum?.updateRelationships(included)
  }

  fun getName(): String? = getData()?.getName()
}
