package com.nativeapptemplate.nativeapptemplatefree.data.shop

import com.nativeapptemplate.nativeapptemplatefree.datastore.NativeAppTemplatePreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.model.*
import com.nativeapptemplate.nativeapptemplatefree.network.Dispatcher
import com.nativeapptemplate.nativeapptemplatefree.network.NativeAppTemplateDispatchers
import com.nativeapptemplate.nativeapptemplatefree.network.apiAction
import com.nativeapptemplate.nativeapptemplatefree.network.apiRequest
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ShopRepositoryImpl @Inject constructor(
  private val natPreferencesDataSource: NativeAppTemplatePreferencesDataSource,
  private val api: ShopApi,
  @param:Dispatcher(NativeAppTemplateDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : ShopRepository {

  override fun getShops() = apiRequest(ioDispatcher) {
    api.getShops(
      natPreferencesDataSource.currentAccountId(),
    )
  }

  override fun getShop(
    id: String,
  ) = apiRequest(ioDispatcher) {
    api.getShop(
      natPreferencesDataSource.currentAccountId(),
      id,
    )
  }

  override fun createShop(
    shopBody: ShopBody,
  ) = apiRequest(ioDispatcher) {
    api.createShop(
      natPreferencesDataSource.currentAccountId(),
      shopBody,
    )
  }

  override fun updateShop(
    id: String,
    shopUpdateBody: ShopUpdateBody,
  ) = apiRequest(ioDispatcher) {
    api.updateShop(
      natPreferencesDataSource.currentAccountId(),
      id,
      shopUpdateBody,
    )
  }

  override fun deleteShop(
    id: String,
  ) = apiAction(ioDispatcher) {
    api.deleteShop(natPreferencesDataSource.currentAccountId(), id)
  }
}
