package com.nativeapptemplate.nativeapptemplatefree.data.login

import androidx.annotation.VisibleForTesting
import com.nativeapptemplate.nativeapptemplatefree.common.errors.ApiException
import com.nativeapptemplate.nativeapptemplatefree.common.errors.logged
import com.nativeapptemplate.nativeapptemplatefree.datastore.NativeAppTemplatePreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.model.*
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Login
import com.nativeapptemplate.nativeapptemplatefree.network.Dispatcher
import com.nativeapptemplate.nativeapptemplatefree.network.NativeAppTemplateDispatchers
import com.nativeapptemplate.nativeapptemplatefree.network.apiAction
import com.nativeapptemplate.nativeapptemplatefree.network.apiRequest
import com.nativeapptemplate.nativeapptemplatefree.network.throwApiError
import com.skydoves.sandwich.message
import com.skydoves.sandwich.suspendOnFailure
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository for session operations
 */
@VisibleForTesting
class LoginRepositoryImpl @Inject constructor(
  private val api: LoginApi,
  private val nativeAppTemplatePreferencesDataSource: NativeAppTemplatePreferencesDataSource,
  @param:Dispatcher(NativeAppTemplateDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : LoginRepository {

  override fun login(
    login: Login,
  ) = apiRequest(ioDispatcher) { api.login(login) }
    .map { it.requireCompleteSession() }
    .flowOn(ioDispatcher)

  override val userData: Flow<UserData> =
    nativeAppTemplatePreferencesDataSource.userData

  override fun logout(notice: SignOutNotice?) = flow {
    val response = api.logout()

    response.suspendOnSuccess {
      clearSession(notice)
      emit(true)
    }.suspendOnFailure {
      clearSession(notice)
      throwApiError(response, message())
    }
  }.flowOn(ioDispatcher)

  override fun getPermissions() = apiRequest(ioDispatcher) {
    api.getPermissions(
      nativeAppTemplatePreferencesDataSource.currentAccountId(),
    )
  }

  override fun updateConfirmedPrivacyVersion() = apiAction(ioDispatcher) {
    api.updateConfirmedPrivacyVersion(
      nativeAppTemplatePreferencesDataSource.currentAccountId(),
    )
  }

  override fun updateConfirmedTermsVersion() = apiAction(ioDispatcher) {
    api.updateConfirmedTermsVersion(
      nativeAppTemplatePreferencesDataSource.currentAccountId(),
    )
  }

  override suspend fun setAccountId(accountId: String) {
    nativeAppTemplatePreferencesDataSource.setAccountId(accountId)
  }

  override suspend fun setShopkeeper(loggedInShopkeeper: LoggedInShopkeeper) {
    nativeAppTemplatePreferencesDataSource.setShopkeeper(loggedInShopkeeper)
  }

  override suspend fun setShopkeeperForUpdate(loggedInShopkeeper: LoggedInShopkeeper) {
    nativeAppTemplatePreferencesDataSource.setShopkeeperForUpdate(loggedInShopkeeper)
  }

  override suspend fun setPermissions(permissions: Permissions) {
    nativeAppTemplatePreferencesDataSource.setPermissions(permissions)
  }

  override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
    nativeAppTemplatePreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
  }

  override suspend fun setDidShowTapShopBelowTip(didShowTapShopBelowTip: Boolean) {
    nativeAppTemplatePreferencesDataSource.setDidShowTapShopBelowTip(didShowTapShopBelowTip)
  }

  override suspend fun setIsEmailUpdated(isEmailUpdated: Boolean) {
    nativeAppTemplatePreferencesDataSource.setIsEmailUpdated(isEmailUpdated)
  }

  override suspend fun setIsMyAccountDeleted(isMyAccountDeleted: Boolean) {
    nativeAppTemplatePreferencesDataSource.setIsMyAccountDeleted(isMyAccountDeleted)
  }

  override suspend fun clearSession(notice: SignOutNotice?) {
    nativeAppTemplatePreferencesDataSource.clearSession(
      isEmailUpdated = notice == SignOutNotice.EMAIL_UPDATED,
      isMyAccountDeleted = notice == SignOutNotice.ACCOUNT_DELETED,
    )
  }

  override fun isLoggedIn(): Flow<Boolean> = nativeAppTemplatePreferencesDataSource.isLoggedIn()

  override fun shouldUpdateApp(): Flow<Boolean> = nativeAppTemplatePreferencesDataSource.shouldUpdateApp()

  override fun shouldUpdatePrivacy(): Flow<Boolean> = nativeAppTemplatePreferencesDataSource.shouldUpdatePrivacy()

  override fun shouldUpdateTerms(): Flow<Boolean> = nativeAppTemplatePreferencesDataSource.shouldUpdateTerms()

  override fun isEmailUpdated(): Flow<Boolean> = nativeAppTemplatePreferencesDataSource.isEmailUpdated()

  override fun isMyAccountDeleted(): Flow<Boolean> = nativeAppTemplatePreferencesDataSource.isMyAccountDeleted()

  override fun didShowTapShopBelowTip(): Flow<Boolean> = nativeAppTemplatePreferencesDataSource.didShowTapShopBelowTip()
}

/**
 * Fails inside the flow, where the caller's `.catch` reports it, when the sign-in response lacks
 * a value the session depends on (auth headers, account id in request paths, ownership checks).
 */
private fun LoggedInShopkeeper.requireCompleteSession(): LoggedInShopkeeper {
  val missing = listOf(
    "id" to getId(),
    "account_id" to getAccountId(),
    "personal_account_id" to getPersonalAccountId(),
    "account_owner_id" to getAccountOwnerId(),
    "token" to getToken(),
    "client" to getClient(),
    "uid" to getUID(),
    "expiry" to getExpiry(),
  ).filter { (_, value) -> value.isNullOrEmpty() }
    .map { (name, _) -> name }

  if (missing.isNotEmpty()) {
    throw ApiException.UnprocessableError(rawMessage = "Incomplete sign-in response: missing ${missing.joinToString()}").logged()
  }
  return this
}
