package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

@Serializable
@Parcelize
data class Shop(
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
  fun getName(): String = getData()?.getName() ?: ""

  fun getDescription(): String = getData()?.getDescription() ?: ""

  fun getTimeZone(): String = getData()?.getTimeZone() ?: TimeZones.DEFAULT_TIME_ZONE
}
