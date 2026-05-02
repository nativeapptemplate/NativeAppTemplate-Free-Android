package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ItemTag(
  @SerialName("data")
  var datum: Data? = null,
  val meta: Meta? = null,
  val included: List<Data>? = null,
) : Parcelable {
  fun getData(): Data? {
    if (included.isNullOrEmpty()) {
      return datum
    }

    return datum?.updateRelationships(included)
  }

  fun getId(): String = getData()?.id ?: ""

  fun getName(): String = getData()?.getName() ?: ""

  fun getDescription(): String = getData()?.getDescription() ?: ""

  fun getPosition(): Int = getData()?.getPosition() ?: 0

  fun getState(): String = getData()?.getState() ?: ""

  fun getShopId(): String = getData()?.getShopId() ?: ""

  fun getShopName(): String = getData()?.getShopName() ?: ""

  fun getCompletedAt(): String? = getData()?.getCompletedAt()

  fun getCreatedAt(): String? = getData()?.getCreatedAt()
}
