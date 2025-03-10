package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

@Serializable
@Parcelize
data class Attributes(
  @SerialName("created_at")
  val createdAt: String? = null,

  @SerialName("updated_at")
  val updatedAt: String? = null,

  val description: String? = null,

  val name: String? = null,

  val url: String? = null,

  val tag: String? = null,

  val state: String? = null,

  @SerialName("queue_number")
  val queueNumber: String? = null,

  @SerialName("scan_state")
  val scanState: String? = null,

  @SerialName("customer_read_at")
  val customerReadAt: String? = null,

  @SerialName("completed_at")
  val completedAt: String? = null,

  @SerialName("already_completed")
  val alreadyCompleted: Boolean? = null,

  @SerialName("account_id")
  val accountId: String? = null,

  @SerialName("personal_account_id")
  val personalAccountId: String? = null,

  @SerialName("account_owner_id")
  val accountOwnerId: String? = null,

  @SerialName("account_name")
  val accountName: String? = null,

  @SerialName("shopkeeper_id")
  val shopkeeperId: String? = null,

  @SerialName("shop_id")
  val shopId: String? = null,

  @SerialName("owner_id")
  val ownerId: String? = null,

  @SerialName("owner_name")
  val ownerName: String? = null,

  @SerialName("is_admin")
  val isAdmin: Boolean? = null,

  @SerialName("shops_count")
  val shopsCount: Int? = null,

  @SerialName("shop_name")
  val shopName: String? = null,

  @SerialName("time_zone")
  val timeZone: String? = null,

  @SerialName("display_shop_server_path")
  val displayShopServerPath: String? = null,

  @SerialName("item_tags_count")
  val itemTagsCount: Int? = null,

  @SerialName("scanned_item_tags_count")
  val scannedItemTagsCount: Int? = null,

  @SerialName("completed_item_tags_count")
  val completedItemTagsCount: Int? = null,

  val email: String? = null,

  val token: String? = null,

  val client: String? = null,

  val uid: String? = null,

  val expiry: String? = null
) : Parcelable
