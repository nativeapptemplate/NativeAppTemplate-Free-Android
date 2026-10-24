package com.nativeapptemplate.nativeapptemplatefree.data.login

import com.nativeapptemplate.nativeapptemplatefree.datastore.NativeAppTemplatePreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.model.*
import com.nativeapptemplate.nativeapptemplatefree.network.Dispatcher
import com.nativeapptemplate.nativeapptemplatefree.network.NativeAppTemplateDispatchers
import com.nativeapptemplate.nativeapptemplatefree.network.apiAction
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AccountPasswordRepositoryImpl @Inject constructor(
  private val nativeAppTemplatePreferencesDataSource: NativeAppTemplatePreferencesDataSource,
  private val api: AccountPasswordApi,
  @param:Dispatcher(NativeAppTemplateDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : AccountPasswordRepository {
  override fun updateAccountPassword(
    updatePasswordBody: UpdatePasswordBody,
  ) = apiAction(ioDispatcher) {
    api.updateAccountPassword(
      nativeAppTemplatePreferencesDataSource.currentAccountId(),
      updatePasswordBody,
    )
  }
}
