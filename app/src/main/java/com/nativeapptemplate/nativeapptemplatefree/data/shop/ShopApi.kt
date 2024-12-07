package com.nativeapptemplate.nativeapptemplatefree.data.shop

import com.nativeapptemplate.nativeapptemplatefree.model.*
import com.skydoves.sandwich.ApiResponse
import retrofit2.Retrofit
import retrofit2.http.*

interface ShopApi {
  @GET("{account_id}/api/v1/shopkeeper/shops")
  suspend fun getShops(
    @Path("account_id") accountId: String,
  ): ApiResponse<Shops>

  @GET("{account_id}/api/v1/shopkeeper/shops/{id}")
  suspend fun getShop(
    @Path("account_id") accountId: String,
    @Path("id") id: String,
  ): ApiResponse<Shop>

  @POST("{account_id}/api/v1/shopkeeper/shops")
  suspend fun createShop(
    @Path("account_id") accountId: String,
    @Body data: ShopBody,
  ): ApiResponse<Shop>

  @PATCH("{account_id}/api/v1/shopkeeper/shops/{id}")
  suspend fun updateShop(
    @Path("account_id") accountId: String,
    @Path("id") id: String,
    @Body data: ShopUpdateBody,
  ): ApiResponse<Shop>

  @DELETE("{account_id}/api/v1/shopkeeper/shops/{id}")
  suspend fun deleteShop(
    @Path("account_id") accountId: String,
    @Path("id") id: String,
  ): ApiResponse<Status>

  companion object {
    fun create(retroFit: Retrofit): ShopApi = retroFit.create(
      ShopApi::class.java
    )
  }
}
