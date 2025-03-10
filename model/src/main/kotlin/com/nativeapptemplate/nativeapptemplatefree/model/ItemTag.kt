package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

@Serializable
@Parcelize
data class ItemTag(
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

  fun getId(): String = getData()?.id ?: ""

  fun getQueueNumber(): String = getData()?.getQueueNumber() ?: ""

  fun getState(): String = getData()?.getState() ?: ""

  fun getScanState(): ScanState = getData()?.getScanState() ?: ScanState.Unscanned

  fun getShopId(): String = getData()?.getShopId() ?: ""

  fun getShopName(): String = getData()?.getShopName() ?: ""

  fun getCustomerReadAt(): String? = getData()?.getCustomerReadAt()

  fun getCompletedAt(): String? = getData()?.getCompletedAt()

  fun getAlreadyCompleted(): Boolean = getData()?.getAlreadyCompleted() ?: false

  fun getCreatedAt(): String? = getData()?.getCreatedAt()
}
