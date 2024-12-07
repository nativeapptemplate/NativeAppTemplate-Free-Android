package com.nativeapptemplate.nativeapptemplatefree.data.shop

import com.nativeapptemplate.nativeapptemplatefree.model.*
import kotlinx.coroutines.flow.Flow

interface ShopRepository {
  fun getShops(): Flow<Shops>

  fun getShop(
    id: String
  ): Flow<Shop>

  fun createShop(
    shopBody: ShopBody
  ): Flow<Shop>

  fun updateShop(
    id: String,
    shopUpdateBody: ShopUpdateBody,
  ): Flow<Shop>

  fun deleteShop(
    id: String,
  ): Flow<Boolean>
}
