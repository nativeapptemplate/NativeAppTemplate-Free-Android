package com.nativeapptemplate.nativeapptemplatefree.data.shop

import com.nativeapptemplate.nativeapptemplatefree.datastore.NatPreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.model.*
import com.nativeapptemplate.nativeapptemplatefree.network.Dispatcher
import com.nativeapptemplate.nativeapptemplatefree.network.NatDispatchers
import com.skydoves.sandwich.message
import com.skydoves.sandwich.retrofit.serialization.deserializeErrorBody
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ShopRepositoryImpl @Inject constructor(
  private val natPreferencesDataSource: NatPreferencesDataSource,
  private val api: ShopApi,
  @Dispatcher(NatDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : ShopRepository {

  override fun getShops(
  ) = flow {
    val response = api.getShops(
      natPreferencesDataSource.userData.first().accountId,
    )

    response.suspendOnSuccess {
      emit(data)
    }.suspendOnFailure {
      val nativeAppTemplateApiError: NativeAppTemplateApiError?

      try {
        nativeAppTemplateApiError = response.deserializeErrorBody<Shops, NativeAppTemplateApiError>()
      } catch (exception: Exception) {
        val message= "Not processable error(${message()})."
        throw Exception(message)
      }

      if (nativeAppTemplateApiError != null) {
        val message= "${nativeAppTemplateApiError.message} [Status: ${nativeAppTemplateApiError.code}]"
        throw Exception(message)
      } else {
        val message= "Not processable error(${message()})."
        throw Exception(message)
      }
    }
  }.flowOn(ioDispatcher)

  override fun getShop(
    id: String,
  ) = flow {
    val response = api.getShop(
      natPreferencesDataSource.userData.first().accountId,
      id
    )
    response.suspendOnSuccess {
      emit(data)
    }.suspendOnFailure {
      val nativeAppTemplateApiError: NativeAppTemplateApiError?

      try {
        nativeAppTemplateApiError = response.deserializeErrorBody<Shop, NativeAppTemplateApiError>()
      } catch (exception: Exception) {
        val message= "Not processable error(${message()})."
        throw Exception(message)
      }

      if (nativeAppTemplateApiError != null) {
        val message= "${nativeAppTemplateApiError.message} [Status: ${nativeAppTemplateApiError.code}]"
        throw Exception(message)
      } else {
        val message= "Not processable error(${message()})."
        throw Exception(message)
      }
    }
  }.flowOn(ioDispatcher)

  override fun createShop(
    shopBody: ShopBody,
  ) = flow {
    var shop: Shop

    val response = api.createShop(
      natPreferencesDataSource.userData.first().accountId,
      shopBody
    )

    response.suspendOnSuccess {
      shop = data
      emit(shop)
    }.suspendOnFailure {
      val nativeAppTemplateApiError: NativeAppTemplateApiError?

      try {
        nativeAppTemplateApiError = response.deserializeErrorBody<Shop, NativeAppTemplateApiError>()
      } catch (exception: Exception) {
        val message= "Not processable error(${message()})."
        throw Exception(message)
      }

      if (nativeAppTemplateApiError != null) {
        val message= "${nativeAppTemplateApiError.message} [Status: ${nativeAppTemplateApiError.code}]"
        throw Exception(message)
      } else {
        val message= "Not processable error(${message()})."
        throw Exception(message)
      }
    }
  }.flowOn(ioDispatcher)

  override fun updateShop(
    id: String,
    shopUpdateBody: ShopUpdateBody,
  ) = flow {
    var shop: Shop

    val response = api.updateShop(
      natPreferencesDataSource.userData.first().accountId,
      id,
      shopUpdateBody,
    )

    response.suspendOnSuccess {
      shop = data
      emit(shop)
    }.suspendOnFailure {
      val nativeAppTemplateApiError: NativeAppTemplateApiError?

      try {
        nativeAppTemplateApiError = response.deserializeErrorBody<Shop, NativeAppTemplateApiError>()
      } catch (exception: Exception) {
        val message= "Not processable error(${message()})."
        throw Exception(message)
      }

      if (nativeAppTemplateApiError != null) {
        val message= "${nativeAppTemplateApiError.message} [Status: ${nativeAppTemplateApiError.code}]"
        throw Exception(message)
      } else {
        val message= "Not processable error(${message()})."
        throw Exception(message)
      }
    }
  }.flowOn(ioDispatcher)

  override fun deleteShop(
    id: String,
  ) = flow {
    val response = api.deleteShop(natPreferencesDataSource.userData.first().accountId, id)

    response.suspendOnSuccess {
      emit(true)
    }.suspendOnFailure {
      val nativeAppTemplateApiError: NativeAppTemplateApiError?

      try {
        nativeAppTemplateApiError = response.deserializeErrorBody<Status, NativeAppTemplateApiError>()
      } catch (exception: Exception) {
        val message= "Not processable error(${message()})."
        throw Exception(message)
      }

      if (nativeAppTemplateApiError != null) {
        val message= "${nativeAppTemplateApiError.message} [Status: ${nativeAppTemplateApiError.code}]"
        throw Exception(message)
      } else {
        val message= "Not processable error(${message()})."
        throw Exception(message)
      }
    }
  }.flowOn(ioDispatcher)
}
