package com.nativeapptemplate.nativeapptemplatefree.testing.repository

import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagBody
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTags
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class TestItemTagRepository : ItemTagRepository {
  private var currentItemTags: ItemTags = ItemTags()

  private val itemTagFlow: MutableSharedFlow<ItemTag> =
    MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  override fun getItemTags(shopId: String, page: Int?): Flow<ItemTags> = flow { emit(currentItemTags) }

  override fun getItemTag(id: String): Flow<ItemTag> = itemTagFlow

  override fun createItemTag(shopId: String, itemTagBody: ItemTagBody): Flow<ItemTag> = itemTagFlow

  override fun updateItemTag(id: String, itemTagBody: ItemTagBody): Flow<ItemTag> = itemTagFlow

  override fun deleteItemTag(id: String): Flow<Boolean> = MutableStateFlow(true)

  override fun completeItemTag(id: String): Flow<ItemTag> = itemTagFlow

  override fun idleItemTag(id: String): Flow<ItemTag> = itemTagFlow

  /**
   * A test-only API.
   */
  fun sendItemTags(itemTags: ItemTags) {
    currentItemTags = itemTags
  }

  fun sendItemTag(itemTag: ItemTag) {
    itemTagFlow.tryEmit(itemTag)
  }
}
