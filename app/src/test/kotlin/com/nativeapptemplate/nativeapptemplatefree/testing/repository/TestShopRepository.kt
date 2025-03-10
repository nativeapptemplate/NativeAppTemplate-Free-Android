package com.nativeapptemplate.nativeapptemplatefree.testing.repository

import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepository
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.model.ShopBody
import com.nativeapptemplate.nativeapptemplatefree.model.ShopUpdateBody
import com.nativeapptemplate.nativeapptemplatefree.model.Shops
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class TestShopRepository : ShopRepository {
  private val shopsFlow: MutableSharedFlow<Shops> =
    MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  private val shopFlow: MutableSharedFlow<Shop> =
    MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  override fun getShops(): Flow<Shops> = shopsFlow

  override fun getShop(id: String): Flow<Shop> = shopFlow

  override fun createShop(shopBody: ShopBody): Flow<Shop> = shopFlow

  override fun updateShop(id: String, shopUpdateBody: ShopUpdateBody): Flow<Shop> = shopFlow

  override fun deleteShop(id: String): Flow<Boolean> =  MutableStateFlow(true)

  override fun resetShop(id: String): Flow<Boolean> =  MutableStateFlow(true)

  /**
   * A test-only API.
   */
  fun sendShops(shops: Shops) {
    shopsFlow.tryEmit(shops)
  }

  fun sendShop(shop: Shop) {
    shopFlow.tryEmit(shop)
  }
}
