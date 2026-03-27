package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Shops(
  @SerialName("data")
  val datum: List<Data> = emptyList(),
  val included: List<Data>? = null,
  val meta: Meta? = null,
) : Parcelable {
  fun getCreatedShopsCount(): Int = meta?.createdShopsCount ?: 0

  fun getLimitCount(): Int = meta?.limitCount ?: 0

  fun getDatumWithRelationships(): List<Data> = datum.map {
    it.updateRelationships(included)
  }
}
