package com.nativeapptemplate.nativeapptemplatefree.data.item_tag

import com.nativeapptemplate.nativeapptemplatefree.model.*
import kotlinx.coroutines.flow.Flow

interface ItemTagRepository {
  fun getItemTags(
    shopId: String,
  ): Flow<ItemTags>

  fun getItemTag(
    id: String,
  ): Flow<ItemTag>

  fun createItemTag(
    shopId: String,
    itemTagBody: ItemTagBody,
  ): Flow<ItemTag>

  fun updateItemTag(
    id: String,
    itemTagBody: ItemTagBody
  ): Flow<ItemTag>

  fun deleteItemTag(
    id: String,
  ): Flow<Boolean>

  fun completeItemTag(
    id: String,
  ): Flow<ItemTag>

  fun resetItemTag(
    id: String,
  ): Flow<ItemTag>
}
