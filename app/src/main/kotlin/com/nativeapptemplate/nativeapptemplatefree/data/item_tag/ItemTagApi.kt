package com.nativeapptemplate.nativeapptemplatefree.data.item_tag

import com.nativeapptemplate.nativeapptemplatefree.model.*
import com.skydoves.sandwich.ApiResponse
import retrofit2.Retrofit
import retrofit2.http.*

interface ItemTagApi {
  @GET("{account_id}/api/v1/shopkeeper/shops/{shop_id}/item_tags")
  suspend fun getItemTags(
    @Path("account_id") accountId: String,
    @Path("shop_id") shopId: String
  ): ApiResponse<ItemTags>

  @GET("{account_id}/api/v1/shopkeeper/item_tags/{id}")
  suspend fun getItemTag(
    @Path("account_id") accountId: String,
    @Path("id") id: String
  ): ApiResponse<ItemTag>

  @POST("{account_id}/api/v1/shopkeeper/shops/{shop_id}/item_tags")
  suspend fun createItemTag(
    @Path("account_id") accountId: String,
    @Path("shop_id") shopId: String,
    @Body data: ItemTagBody
  ): ApiResponse<ItemTag>

  @PATCH("{account_id}/api/v1/shopkeeper/item_tags/{id}")
  suspend fun updateItemTag(
    @Path("account_id") accountId: String,
    @Path("id") id: String,
    @Body data: ItemTagBody
  ): ApiResponse<ItemTag>

  @DELETE("{account_id}/api/v1/shopkeeper/item_tags/{id}")
  suspend fun deleteItemTag(
    @Path("account_id") accountId: String,
    @Path("id") id: String
  ): ApiResponse<Status>

  @PATCH("{account_id}/api/v1/shopkeeper/item_tags/{id}/complete")
  suspend fun completeItemTag(
    @Path("account_id") accountId: String,
    @Path("id") id: String,
  ): ApiResponse<ItemTag>

  @PATCH("{account_id}/api/v1/shopkeeper/item_tags/{id}/reset")
  suspend fun resetItemTag(
    @Path("account_id") accountId: String,
    @Path("id") id: String,
  ): ApiResponse<ItemTag>

  companion object {
    fun create(retroFit: Retrofit): ItemTagApi = retroFit.create(
      ItemTagApi::class.java
    )
  }
}
