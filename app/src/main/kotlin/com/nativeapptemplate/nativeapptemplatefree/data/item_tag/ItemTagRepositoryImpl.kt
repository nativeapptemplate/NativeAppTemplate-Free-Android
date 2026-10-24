package com.nativeapptemplate.nativeapptemplatefree.data.item_tag

import com.nativeapptemplate.nativeapptemplatefree.datastore.NativeAppTemplatePreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.model.*
import com.nativeapptemplate.nativeapptemplatefree.network.Dispatcher
import com.nativeapptemplate.nativeapptemplatefree.network.NativeAppTemplateDispatchers
import com.nativeapptemplate.nativeapptemplatefree.network.apiAction
import com.nativeapptemplate.nativeapptemplatefree.network.apiRequest
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ItemTagRepositoryImpl @Inject constructor(
  private val nativeAppTemplatePreferencesDataSource: NativeAppTemplatePreferencesDataSource,
  private val api: ItemTagApi,
  @param:Dispatcher(NativeAppTemplateDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : ItemTagRepository {

  override fun getItemTags(
    shopId: String,
    page: Int?,
  ) = apiRequest(ioDispatcher) {
    api.getItemTags(
      nativeAppTemplatePreferencesDataSource.currentAccountId(),
      shopId,
      page,
    )
  }

  override fun getItemTag(
    id: String,
  ) = apiRequest(ioDispatcher) {
    api.getItemTag(
      nativeAppTemplatePreferencesDataSource.currentAccountId(),
      id,
    )
  }

  override fun createItemTag(
    shopId: String,
    itemTagBody: ItemTagBody,
  ) = apiRequest(ioDispatcher) {
    api.createItemTag(
      nativeAppTemplatePreferencesDataSource.currentAccountId(),
      shopId,
      itemTagBody,
    )
  }

  override fun updateItemTag(
    id: String,
    itemTagBody: ItemTagBody,
  ) = apiRequest(ioDispatcher) {
    api.updateItemTag(
      nativeAppTemplatePreferencesDataSource.currentAccountId(),
      id,
      itemTagBody,
    )
  }

  override fun deleteItemTag(
    id: String,
  ) = apiAction(ioDispatcher) {
    api.deleteItemTag(nativeAppTemplatePreferencesDataSource.currentAccountId(), id)
  }

  override fun completeItemTag(
    id: String,
  ) = apiRequest(ioDispatcher) {
    api.completeItemTag(nativeAppTemplatePreferencesDataSource.currentAccountId(), id)
  }

  override fun idleItemTag(
    id: String,
  ) = apiRequest(ioDispatcher) {
    api.idleItemTag(nativeAppTemplatePreferencesDataSource.currentAccountId(), id)
  }
}
