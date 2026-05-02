package com.nativeapptemplate.nativeapptemplatefree.data.shop

import com.nativeapptemplate.nativeapptemplatefree.datastore.NativeAppTemplatePreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.model.*
import com.nativeapptemplate.nativeapptemplatefree.network.Dispatcher
import com.nativeapptemplate.nativeapptemplatefree.network.NativeAppTemplateDispatchers
import com.nativeapptemplate.nativeapptemplatefree.network.emitApiResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ShopRepositoryImpl @Inject constructor(
  private val natPreferencesDataSource: NativeAppTemplatePreferencesDataSource,
  private val api: ShopApi,
  @Dispatcher(NativeAppTemplateDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : ShopRepository {

  override fun getShops() = flow {
    val response = api.getShops(
      natPreferencesDataSource.userData.first().accountId,
    )
    emitApiResponse(response)
  }.flowOn(ioDispatcher)

  override fun getShop(
    id: String,
  ) = flow {
    val response = api.getShop(
      natPreferencesDataSource.userData.first().accountId,
      id,
    )
    emitApiResponse(response)
  }.flowOn(ioDispatcher)

  override fun createShop(
    shopBody: ShopBody,
  ) = flow {
    val response = api.createShop(
      natPreferencesDataSource.userData.first().accountId,
      shopBody,
    )
    emitApiResponse(response)
  }.flowOn(ioDispatcher)

  override fun updateShop(
    id: String,
    shopUpdateBody: ShopUpdateBody,
  ) = flow {
    val response = api.updateShop(
      natPreferencesDataSource.userData.first().accountId,
      id,
      shopUpdateBody,
    )
    emitApiResponse(response)
  }.flowOn(ioDispatcher)

  override fun deleteShop(
    id: String,
  ) = flow {
    val response = api.deleteShop(natPreferencesDataSource.userData.first().accountId, id)
    emitApiResponse<Status, Boolean>(response) { true }
  }.flowOn(ioDispatcher)
}
