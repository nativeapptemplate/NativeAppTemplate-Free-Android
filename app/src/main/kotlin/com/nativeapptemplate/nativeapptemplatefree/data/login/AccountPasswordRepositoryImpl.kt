package com.nativeapptemplate.nativeapptemplatefree.data.login

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

class AccountPasswordRepositoryImpl @Inject constructor(
  private val natPreferencesDataSource: NatPreferencesDataSource,
  private val api: AccountPasswordApi,
  @Dispatcher(NatDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : AccountPasswordRepository {
  override fun updateAccountPassword(
    updatePasswordBody: UpdatePasswordBody,
  ) = flow {
    val response = api.updateAccountPassword(
      natPreferencesDataSource.userData.first().accountId,
      updatePasswordBody
    )

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
