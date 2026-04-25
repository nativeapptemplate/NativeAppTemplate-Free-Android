package com.nativeapptemplate.nativeapptemplatefree.demo.shop

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepository
import com.nativeapptemplate.nativeapptemplatefree.demo.DemoAssetManager
import com.nativeapptemplate.nativeapptemplatefree.demo.DemoAssetManagerImpl
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.model.ShopBody
import com.nativeapptemplate.nativeapptemplatefree.model.ShopUpdateBody
import com.nativeapptemplate.nativeapptemplatefree.model.Shops
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

class DemoShopRepository @Inject constructor(
  @Dispatcher(NatDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  private val networkJson: Json,
  private val assets: DemoAssetManager = DemoAssetManagerImpl,
) : ShopRepository {
  private val shopsFlow: Flow<Shops> = flow {
    val shops = getDataFromJsonFile<Shops>(SHOPS_ASSET)
    emit(shops)
  }.flowOn(ioDispatcher)

  private val shopFlow: Flow<Shop> = flow {
    val shop = getDataFromJsonFile<Shop>(SHOP_ASSET)
    emit(shop)
  }.flowOn(ioDispatcher)

  override fun getShops(): Flow<Shops> = shopsFlow

  override fun getShop(id: String): Flow<Shop> = shopFlow

  override fun createShop(shopBody: ShopBody): Flow<Shop> = shopFlow

  override fun updateShop(id: String, shopUpdateBody: ShopUpdateBody): Flow<Shop> = shopFlow

  override fun deleteShop(id: String): Flow<Boolean> = MutableStateFlow(true)

  @OptIn(ExperimentalSerializationApi::class)
  private suspend inline fun <reified T> getDataFromJsonFile(fileName: String): T =
    withContext(ioDispatcher) {
      val context = ApplicationProvider.getApplicationContext<Context>()
      assets.open(context, fileName).use { inputStream ->
        networkJson.decodeFromStream(inputStream)
      }
    }

  companion object {
    private const val SHOPS_ASSET = "shops.json"
    private const val SHOP_ASSET = "shop.json"
  }
}
