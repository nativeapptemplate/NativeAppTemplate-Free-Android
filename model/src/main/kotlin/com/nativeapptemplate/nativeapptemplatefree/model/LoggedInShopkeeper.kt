package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

@Serializable
@Parcelize
data class LoggedInShopkeeper(
  @SerialName("data")
  var datum: Data? = null,

  val included: List<Data>? = null,

  val meta: Meta? = null
) : Parcelable {

  fun getId(): String? = getData()?.id

  fun getAccountId(): String? = getData()?.getAccountId()

  fun getPersonalAccountId(): String? = getData()?.getPersonalAccountId()

  fun getAccountOwnerId(): String? = getData()?.getAccountOwnerId()

  fun getAccountName(): String? = getData()?.getCurrentAccountName()

  fun getEmail(): String? = getData()?.getEmail()

  fun getName(): String? = getData()?.getName()

  fun getTimeZone(): String? = getData()?.getTimeZone()

  fun getToken(): String? = getData()?.getToken()

  fun getClient(): String? = getData()?.getClient()

  fun getUID(): String? = getData()?.getUID()

  fun getExpiry(): String? = getData()?.getExpiry()

  private fun getData(): Data? = datum
}
