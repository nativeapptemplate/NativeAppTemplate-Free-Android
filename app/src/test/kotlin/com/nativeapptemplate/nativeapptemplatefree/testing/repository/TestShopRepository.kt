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

class TestShopRepository :
  ShopRepository,
  FailableRepository {
  override val failures = CallFailures()

  private val shopsFlow: MutableSharedFlow<Shops> =
    MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  private val shopFlow: MutableSharedFlow<Shop> =
    MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  override fun getShops(): Flow<Shops> = failures.orFail(ShopRepository::getShops, shopsFlow)

  override fun getShop(id: String): Flow<Shop> = failures.orFail(ShopRepository::getShop, shopFlow)

  override fun createShop(shopBody: ShopBody): Flow<Shop> = failures.orFail(ShopRepository::createShop, shopFlow)

  override fun updateShop(id: String, shopUpdateBody: ShopUpdateBody): Flow<Shop> = failures.orFail(ShopRepository::updateShop, shopFlow)

  override fun deleteShop(id: String): Flow<Boolean> = failures.orFail(ShopRepository::deleteShop, MutableStateFlow(true))

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
