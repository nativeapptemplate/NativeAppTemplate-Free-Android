package com.nativeapptemplate.nativeapptemplatefree.data.login

import androidx.annotation.VisibleForTesting
import com.nativeapptemplate.nativeapptemplatefree.common.errors.ApiException
import com.nativeapptemplate.nativeapptemplatefree.datastore.NatPreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.model.*
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Login
import com.nativeapptemplate.nativeapptemplatefree.network.Dispatcher
import com.nativeapptemplate.nativeapptemplatefree.network.NatDispatchers
import com.nativeapptemplate.nativeapptemplatefree.network.emitApiResponse
import com.skydoves.sandwich.message
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Repository for session operations
 */
@VisibleForTesting
class LoginRepositoryImpl @Inject constructor(
  private val api: LoginApi,
  private val natPreferencesDataSource: NatPreferencesDataSource,
  @Dispatcher(NatDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : LoginRepository {

  override fun login(
    login: Login,
  ) = flow {
    val response = api.login(login)
    emitApiResponse(response)
  }.flowOn(ioDispatcher)

  override val userData: Flow<UserData> =
    natPreferencesDataSource.userData

  override fun logout() = flow {
    val response = api.logout()

    response.suspendOnSuccess {
      clearUserPreferences()
      emit(true)
    }.suspendOnFailure {
      clearUserPreferences()
      throw ApiException.UnprocessableError(rawMessage = message())
    }
  }.flowOn(ioDispatcher)

  override fun getPermissions() = flow {
    val response = api.getPermissions(
      userData.first().accountId,
    )
    emitApiResponse(response)
  }.flowOn(ioDispatcher)

  override fun updateConfirmedPrivacyVersion() = flow {
    val response = api.updateConfirmedPrivacyVersion(
      natPreferencesDataSource.userData.first().accountId,
    )
    emitApiResponse<Status, Boolean>(response) { true }
  }.flowOn(ioDispatcher)

  override fun updateConfirmedTermsVersion() = flow {
    val response = api.updateConfirmedTermsVersion(
      natPreferencesDataSource.userData.first().accountId,
    )
    emitApiResponse<Status, Boolean>(response) { true }
  }.flowOn(ioDispatcher)

  override suspend fun setAccountId(accountId: String) {
    natPreferencesDataSource.setAccountId(accountId)
  }

  override suspend fun setShopkeeper(loggedInShopkeeper: LoggedInShopkeeper) {
    natPreferencesDataSource.setShopkeeper(loggedInShopkeeper)
  }

  override suspend fun setShopkeeperForUpdate(loggedInShopkeeper: LoggedInShopkeeper) {
    natPreferencesDataSource.setShopkeeperForUpdate(loggedInShopkeeper)
  }

  override suspend fun setPermissions(permissions: Permissions) {
    natPreferencesDataSource.setPermissions(permissions)
  }

  override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
    natPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
  }

  override suspend fun setDidShowTapShopBelowTip(didShowTapShopBelowTip: Boolean) {
    natPreferencesDataSource.setDidShowTapShopBelowTip(didShowTapShopBelowTip)
  }

  override suspend fun setIsEmailUpdated(isEmailUpdated: Boolean) {
    natPreferencesDataSource.setIsEmailUpdated(isEmailUpdated)
  }

  override suspend fun setIsMyAccountDeleted(isMyAccountDeleted: Boolean) {
    natPreferencesDataSource.setIsMyAccountDeleted(isMyAccountDeleted)
  }

  override suspend fun setIsShopDeleted(isShopDeleted: Boolean) {
    natPreferencesDataSource.setIsShopDeleted(isShopDeleted)
  }

  override suspend fun clearUserPreferences() {
    natPreferencesDataSource.clearUserPreferences()
  }

  override fun isLoggedIn(): Flow<Boolean> = natPreferencesDataSource.isLoggedIn()

  override fun shouldUpdateApp(): Flow<Boolean> = natPreferencesDataSource.shouldUpdateApp()

  override fun shouldUpdatePrivacy(): Flow<Boolean> = natPreferencesDataSource.shouldUpdatePrivacy()

  override fun shouldUpdateTerms(): Flow<Boolean> = natPreferencesDataSource.shouldUpdateTerms()

  override fun isEmailUpdated(): Flow<Boolean> = natPreferencesDataSource.isEmailUpdated()

  override fun isMyAccountDeleted(): Flow<Boolean> = natPreferencesDataSource.isMyAccountDeleted()

  override fun isShopDeleted(): Flow<Boolean> = natPreferencesDataSource.isShopDeleted()

  override fun didShowTapShopBelowTip(): Flow<Boolean> = natPreferencesDataSource.didShowTapShopBelowTip()

  override fun getMaximumQueueNumberLength(): Flow<Int> = natPreferencesDataSource.getMaximumQueueNumberLength()
}
