package com.nativeapptemplate.nativeapptemplatefree.data.item_tag

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

    response.suspendOnSuccess {
      emit(data)
    }.suspendOnFailure {
      val nativeAppTemplateApiError: NativeAppTemplateApiError?

      try {
        nativeAppTemplateApiError = response.deserializeErrorBody<ItemTags, NativeAppTemplateApiError>()
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

  override fun getItemTag(
    id: String,
  ) = flow {
    val response = api.getItemTag(
      mtcPreferencesDataSource.userData.first().accountId,
      id
    )

    response.suspendOnSuccess {
      emit(data)
    }.suspendOnFailure {
      val nativeAppTemplateApiError: NativeAppTemplateApiError?

      try {
        nativeAppTemplateApiError = response.deserializeErrorBody<ItemTag, NativeAppTemplateApiError>()
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

  override fun createItemTag(
    shopId: String,
    itemTagBody: ItemTagBody,
  ) = flow {
    var itemTag: ItemTag

    val response = api.createItemTag(
      mtcPreferencesDataSource.userData.first().accountId,
      shopId,
      itemTagBody,
    )

    response.suspendOnSuccess {
      itemTag = data
      emit(itemTag)
    }.suspendOnFailure {
      val nativeAppTemplateApiError: NativeAppTemplateApiError?

      try {
        nativeAppTemplateApiError = response.deserializeErrorBody<ItemTag, NativeAppTemplateApiError>()
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

  override fun updateItemTag(
    id: String,
    itemTagBody: ItemTagBody,
  ) = flow {
    var itemTag: ItemTag

    val response = api.updateItemTag(
      mtcPreferencesDataSource.userData.first().accountId,
      id,
      itemTagBody
    )

    response.suspendOnSuccess {
      itemTag = data
      emit(itemTag)
    }.suspendOnFailure {
      val nativeAppTemplateApiError: NativeAppTemplateApiError?

      try {
        nativeAppTemplateApiError = response.deserializeErrorBody<ItemTag, NativeAppTemplateApiError>()
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

  override fun deleteItemTag(
    id: String,
  ) = flow {
    val response = api.deleteItemTag(mtcPreferencesDataSource.userData.first().accountId, id)

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

  override fun completeItemTag(
    id: String,
  ) = flow {
    val response = api.completeItemTag(mtcPreferencesDataSource.userData.first().accountId, id)

    response.suspendOnSuccess {
      emit(data)
    }.suspendOnFailure {
      val nativeAppTemplateApiError: NativeAppTemplateApiError?

      try {
        nativeAppTemplateApiError = response.deserializeErrorBody<ItemTag, NativeAppTemplateApiError>()
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

  override fun resetItemTag(
    id: String,
  ) = flow {
    val response = api.resetItemTag(mtcPreferencesDataSource.userData.first().accountId, id)

    response.suspendOnSuccess {
      emit(data)
    }.suspendOnFailure {
      val nativeAppTemplateApiError: NativeAppTemplateApiError?

      try {
        nativeAppTemplateApiError = response.deserializeErrorBody<ItemTag, NativeAppTemplateApiError>()
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
