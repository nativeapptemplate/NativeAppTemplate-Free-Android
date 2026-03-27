package com.nativeapptemplate.nativeapptemplatefree.data.login

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
      updatePasswordBody,
    )
    emitApiResponse<Status, Boolean>(response) { true }
  }.flowOn(ioDispatcher)
}
