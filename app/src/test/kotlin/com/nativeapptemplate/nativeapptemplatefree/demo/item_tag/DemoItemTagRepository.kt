package com.nativeapptemplate.nativeapptemplatefree.demo.item_tag

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.demo.DemoAssetManager
import com.nativeapptemplate.nativeapptemplatefree.demo.DemoAssetManagerImpl
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagBody
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTags
import com.nativeapptemplate.nativeapptemplatefree.network.Dispatcher
import com.nativeapptemplate.nativeapptemplatefree.network.NatDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

class DemoItemTagRepository @Inject constructor(
  @Dispatcher(NatDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  private val networkJson: Json,
  private val assets: DemoAssetManager = DemoAssetManagerImpl,
) : ItemTagRepository {
  private val itemTagsFlow: Flow<ItemTags> = flow {
    val itemTags = getDataFromJsonFile<ItemTags>(ITEM_TAGS_ASSET)
    emit(itemTags)
  }.flowOn(ioDispatcher)

  private val itemTagFlow: Flow<ItemTag> = flow {
    val itemTag = getDataFromJsonFile<ItemTag>(ITEM_TAG_ASSET)
    emit(itemTag)
  }.flowOn(ioDispatcher)

  override fun getItemTags(shopId: String): Flow<ItemTags> = itemTagsFlow

  override fun getItemTag(id: String): Flow<ItemTag> = itemTagFlow

  override fun createItemTag(shopId: String, itemTagBody: ItemTagBody): Flow<ItemTag> = itemTagFlow

  override fun updateItemTag(id: String, itemTagBody: ItemTagBody): Flow<ItemTag> = itemTagFlow

  override fun deleteItemTag(id: String): Flow<Boolean> = MutableStateFlow(true)

  override fun completeItemTag(id: String): Flow<ItemTag> = itemTagFlow

  override fun resetItemTag(id: String): Flow<ItemTag> = itemTagFlow

  @OptIn(ExperimentalSerializationApi::class)
  private suspend inline fun <reified T> getDataFromJsonFile(fileName: String): T =
    withContext(ioDispatcher) {
      val context = ApplicationProvider.getApplicationContext<Context>()
      assets.open(context, fileName).use { inputStream ->
        networkJson.decodeFromStream(inputStream)
      }
    }

  companion object {
    private const val ITEM_TAGS_ASSET = "item_tags.json"
    private const val ITEM_TAG_ASSET = "item_tag.json"
  }
}
