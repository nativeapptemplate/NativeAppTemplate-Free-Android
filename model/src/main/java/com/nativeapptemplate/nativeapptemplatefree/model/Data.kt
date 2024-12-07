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
) : Parcelable {
  fun getName(): String = attributes?.name ?: ""

  fun getEmail(): String = attributes?.email ?: ""

  fun getDescription(): String = attributes?.description ?: ""

  fun updateRelationships(updatedRelations: List<Data>?): Data {
    if (updatedRelations.isNullOrEmpty()) {
      return this
    }

    val updatedRelationships = relationships

    return this.copy(relationships = updatedRelationships)
  }

  fun getToken(): String? = attributes?.token

  fun getClient(): String? = attributes?.client

  fun getExpiry(): String? = attributes?.expiry

  fun getUID(): String? = attributes?.uid

  fun getAccountId(): String? = attributes?.accountId

  fun getPersonalAccountId(): String? = attributes?.personalAccountId

  fun getAccountOwnerId(): String? = attributes?.accountOwnerId

  fun getCurrentAccountName(): String? = attributes?.accountName

  fun getTimeZone(): String = attributes?.timeZone ?: TimeZones.DEFAULT_TIME_ZONE
}
