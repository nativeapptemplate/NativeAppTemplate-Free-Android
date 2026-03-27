package com.nativeapptemplate.nativeapptemplatefree.data.item_tag

import com.nativeapptemplate.nativeapptemplatefree.datastore.NatPreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.model.*
import com.nativeapptemplate.nativeapptemplatefree.network.Dispatcher
import com.nativeapptemplate.nativeapptemplatefree.network.NatDispatchers
import com.nativeapptemplate.nativeapptemplatefree.network.emitApiResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ItemTagRepositoryImpl @Inject constructor(
  private val mtcPreferencesDataSource: NatPreferencesDataSource,
  private val api: ItemTagApi,
  @Dispatcher(NatDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : ItemTagRepository {

  override fun getItemTags(
    shopId: String,
  ) = flow {
    val response = api.getItemTags(
      mtcPreferencesDataSource.userData.first().accountId,
      shopId,
    )
    emitApiResponse(response)
  }.flowOn(ioDispatcher)

  override fun getItemTag(
    id: String,
  ) = flow {
    val response = api.getItemTag(
      mtcPreferencesDataSource.userData.first().accountId,
      id,
    )
    emitApiResponse(response)
  }.flowOn(ioDispatcher)

  override fun createItemTag(
    shopId: String,
    itemTagBody: ItemTagBody,
  ) = flow {
    val response = api.createItemTag(
      mtcPreferencesDataSource.userData.first().accountId,
      shopId,
      itemTagBody,
    )
    emitApiResponse(response)
  }.flowOn(ioDispatcher)

  override fun updateItemTag(
    id: String,
    itemTagBody: ItemTagBody,
  ) = flow {
    val response = api.updateItemTag(
      mtcPreferencesDataSource.userData.first().accountId,
      id,
      itemTagBody,
    )
    emitApiResponse(response)
  }.flowOn(ioDispatcher)

  override fun deleteItemTag(
    id: String,
  ) = flow {
    val response = api.deleteItemTag(mtcPreferencesDataSource.userData.first().accountId, id)
    emitApiResponse<Status, Boolean>(response) { true }
  }.flowOn(ioDispatcher)

  override fun completeItemTag(
    id: String,
  ) = flow {
    val response = api.completeItemTag(mtcPreferencesDataSource.userData.first().accountId, id)
    emitApiResponse(response)
  }.flowOn(ioDispatcher)

  override fun resetItemTag(
    id: String,
  ) = flow {
    val response = api.resetItemTag(mtcPreferencesDataSource.userData.first().accountId, id)
    emitApiResponse(response)
  }.flowOn(ioDispatcher)
}
