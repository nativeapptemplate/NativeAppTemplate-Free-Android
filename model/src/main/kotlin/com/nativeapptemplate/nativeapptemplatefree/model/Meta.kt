package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Meta(
  @SerialName("limit_count")
  var limitCount: Int = 0,

  @SerialName("created_shops_count")
  var createdShopsCount: Int = 0,

  @SerialName("android_app_version")
  var androidAppVersion: Int = 0,

  @SerialName("should_update_privacy")
  val shouldUpdatePrivacy: Boolean? = null,

  @SerialName("should_update_terms")
  var shouldUpdateTerms: Boolean? = null,

  @SerialName("maximum_queue_number_length")
  var maximumQueueNumberLength: Int = 0,

  @SerialName("shop_limit_count")
  var shopLimitCount: Int = 0,

  var count: Int = 0,

  @SerialName("current_page")
  val currentPage: Int? = null,

  @SerialName("total_pages")
  val totalPages: Int? = null,

  @SerialName("total_count")
  val totalCount: Int? = null,

  val limit: Int? = null,
) : Parcelable
