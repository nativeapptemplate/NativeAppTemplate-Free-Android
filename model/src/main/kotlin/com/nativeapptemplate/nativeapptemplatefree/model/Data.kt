package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

@Serializable
@Parcelize
data class Data(
  val id: String? = null,
  val type: String? = null,
  val attributes: Attributes? = null,
  val relationships: Relationships? = null,
  val meta: Meta? = null,
  val included: Shops? = null,
  val isOptionsRevealed: Boolean = false,
) : Parcelable {
  fun getCreatedAt(): String = attributes?.createdAt ?: ""

  fun getName(): String = attributes?.name ?: ""

  fun getState(): String? = attributes?.state

  fun getEmail(): String = attributes?.email ?: ""

  fun getDescription(): String = attributes?.description ?: ""

  fun updateRelationships(updatedRelations: List<Data>?): Data {
    if (updatedRelations.isNullOrEmpty()) {
      return this
    }

    val updatedRelationships = relationships

    return this.copy(relationships = updatedRelationships)
  }

  fun getItemTagState(): ItemTagState? {
    return ItemTagState.fromParam(getState())
  }

  fun getScanState(): ScanState? {
    return ScanState.fromParam(attributes?.scanState)
  }

  fun getToken(): String? = attributes?.token

  fun getClient(): String? = attributes?.client

  fun getExpiry(): String? = attributes?.expiry

  fun getUID(): String? = attributes?.uid

  fun getAccountId(): String? = attributes?.accountId

  fun getPersonalAccountId(): String? = attributes?.personalAccountId

  fun getAccountOwnerId(): String? = attributes?.accountOwnerId

  fun getCurrentAccountName(): String? = attributes?.accountName

  fun getQueueNumber(): String = attributes?.queueNumber ?: ""

  fun getCustomerReadAt(): String = attributes?.customerReadAt ?: ""

  fun getAlreadyCompleted(): Boolean = attributes?.alreadyCompleted ?: false

  fun getCompletedAt(): String = attributes?.completedAt ?: ""

  fun getShopId(): String? = attributes?.shopId

  fun getShopName(): String? = attributes?.shopName

  fun getTimeZone(): String = attributes?.timeZone ?: TimeZones.DEFAULT_TIME_ZONE

  fun getItemTagsCount(): Int = attributes?.itemTagsCount ?: 0

  fun getScannedItemTagsCount(): Int = attributes?.scannedItemTagsCount ?: 0

  fun getCompletedItemTagsCount(): Int = attributes?.completedItemTagsCount ?: 0

  fun getDisplayShopServerPath(): String = attributes?.displayShopServerPath ?: ""
}
