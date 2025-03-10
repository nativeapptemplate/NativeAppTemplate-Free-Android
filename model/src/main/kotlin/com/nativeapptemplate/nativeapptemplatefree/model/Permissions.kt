package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

@Serializable
@Parcelize
data class Permissions(
  @SerialName("data")
  val datum: List<Data> = emptyList(),

  val included: List<Data>? = null,

  val meta: Meta? = null
) : Parcelable {
  fun getAndroidAppVersion(): Int? = meta?.androidAppVersion

  fun getShouldUpdatePrivacy(): Boolean? = meta?.shouldUpdatePrivacy

  fun getShouldUpdateTerms(): Boolean? = meta?.shouldUpdateTerms

  fun getMaximumQueueNumberLength(): Int? = meta?.maximumQueueNumberLength

  fun getShopLimitCount(): Int? = meta?.shopLimitCount
}
