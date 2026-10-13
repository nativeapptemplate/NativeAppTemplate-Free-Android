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

class TestItemTagRepository :
  ItemTagRepository,
  FailableRepository {
  override val failures = CallFailures()

  private var currentItemTags: ItemTags = ItemTags()

  private val itemTagFlow: MutableSharedFlow<ItemTag> =
    MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  /** Pages requested through [getItemTags], in order. */
  val requestedPages = mutableListOf<Int?>()

  /** When set, [getItemTags] serves each page from this instead of [sendItemTags]. */
  var itemTagsForPage: ((Int?) -> ItemTags)? = null

  override fun getItemTags(shopId: String, page: Int?): Flow<ItemTags> = failures.orFail(
    ItemTagRepository::getItemTags,
    flow {
      emit(itemTagsForPage?.invoke(page) ?: currentItemTags)
    },
  ).also { requestedPages += page }

  /** How many times [getItemTag] was called. */
  var getItemTagCallCount = 0
    private set

  override fun getItemTag(id: String): Flow<ItemTag> = failures.orFail(ItemTagRepository::getItemTag, itemTagFlow)
    .also { getItemTagCallCount++ }

  override fun createItemTag(shopId: String, itemTagBody: ItemTagBody): Flow<ItemTag> = failures.orFail(ItemTagRepository::createItemTag, itemTagFlow)

  override fun updateItemTag(id: String, itemTagBody: ItemTagBody): Flow<ItemTag> = failures.orFail(ItemTagRepository::updateItemTag, itemTagFlow)

  override fun deleteItemTag(id: String): Flow<Boolean> = failures.orFail(ItemTagRepository::deleteItemTag, MutableStateFlow(true))

  override fun completeItemTag(id: String): Flow<ItemTag> = failures.orFail(ItemTagRepository::completeItemTag, itemTagFlow)

  override fun idleItemTag(id: String): Flow<ItemTag> = failures.orFail(ItemTagRepository::idleItemTag, itemTagFlow)

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
