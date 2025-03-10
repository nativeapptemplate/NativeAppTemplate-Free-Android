package com.nativeapptemplate.nativeapptemplatefree.data.login

import androidx.annotation.VisibleForTesting
import com.nativeapptemplate.nativeapptemplatefree.datastore.NatPreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.model.*
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Login
import com.nativeapptemplate.nativeapptemplatefree.network.Dispatcher
import com.nativeapptemplate.nativeapptemplatefree.network.NatDispatchers
import com.skydoves.sandwich.message
import com.skydoves.sandwich.retrofit.serialization.deserializeErrorBody
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
    login: Login
  ) = flow {
    var loggedInShopkeeper: LoggedInShopkeeper

    val response = api.login(login)

    response.suspendOnSuccess {
      loggedInShopkeeper = data
      emit(loggedInShopkeeper)
    }.suspendOnFailure {
      val nativeAppTemplateApiError: NativeAppTemplateApiError?

      try {
        nativeAppTemplateApiError = response.deserializeErrorBody<LoggedInShopkeeper, NativeAppTemplateApiError>()
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

  override val userData: Flow<UserData> =
    natPreferencesDataSource.userData

  override fun logout() = flow {
    val response = api.logout()

    response.suspendOnSuccess {
      clearUserPreferences()
      emit(true)
    }.suspendOnFailure {
      clearUserPreferences()
      throw Exception(message())
    }
  }.flowOn(ioDispatcher)

  override fun getPermissions(
  ) = flow {
    val response = api.getPermissions(
      userData.first().accountId
    )

    response.suspendOnSuccess {
      emit(data)
    }.suspendOnFailure {
      val nativeAppTemplateApiError: NativeAppTemplateApiError?

      try {
        nativeAppTemplateApiError = response.deserializeErrorBody<Permissions, NativeAppTemplateApiError>()
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

  override fun updateConfirmedPrivacyVersion(
  ) = flow {
    val response = api.updateConfirmedPrivacyVersion(
      natPreferencesDataSource.userData.first().accountId,
    )

    response.suspendOnSuccess {
      emit(true)
    }.suspendOnFailure { // handles the all error cases from the API request fails.
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

  override fun updateConfirmedTermsVersion(
  ) = flow {
    val response = api.updateConfirmedTermsVersion(
      natPreferencesDataSource.userData.first().accountId,
    )

    response.suspendOnSuccess {
      emit(true)
    }.suspendOnFailure { // handles the all error cases from the API request fails.
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

  override suspend fun setShouldFetchItemTagForShowTagInfoScan(shouldFetchItemTagForShowTagInfoScan: Boolean) {
    natPreferencesDataSource.setShouldFetchItemTagForShowTagInfoScan(shouldFetchItemTagForShowTagInfoScan)
  }

  override suspend fun setShouldCompleteItemTagForCompleteScan(shouldCompleteItemTagForCompleteScan: Boolean) {
    natPreferencesDataSource.setShouldCompleteItemTagForCompleteScan(shouldCompleteItemTagForCompleteScan)
  }

  override suspend fun setShouldNavigateToScanView(shouldNavigateToScanView: Boolean) {
    natPreferencesDataSource.setShouldNavigateToScanView(shouldNavigateToScanView)
  }

  override suspend fun setScanViewSelectedTabIndex(scanViewSelectedTabIndex: Int) {
    natPreferencesDataSource.setScanViewSelectedTabIndex(scanViewSelectedTabIndex)
  }

  override suspend fun setCompleteScanResult(completeScanResult: CompleteScanResult) {
    natPreferencesDataSource.setCompleteScanResult(completeScanResult)
  }

  override suspend fun setShowTagInfoScanResult(showTagInfoScanResult: ShowTagInfoScanResult) {
    natPreferencesDataSource.setShowTagInfoScanResult(showTagInfoScanResult)
  }

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

  override suspend fun setDidShowReadInstructionsTip(didShowReadInstructionsTip: Boolean) {
    natPreferencesDataSource.setDidShowReadInstructionsTip(didShowReadInstructionsTip)
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

  override fun didShowReadInstructionsTip(): Flow<Boolean> = natPreferencesDataSource.didShowReadInstructionsTip()

  override fun getMaximumQueueNumberLength(): Flow<Int> = natPreferencesDataSource.getMaximumQueueNumberLength()

  override fun shouldFetchItemTagForShowTagInfoScan(): Flow<Boolean> = natPreferencesDataSource.shouldFetchItemTagForShowTagInfoScan()

  override fun shouldCompleteItemTagForCompleteScan(): Flow<Boolean> = natPreferencesDataSource.shouldCompleteItemTagForCompleteScan()

  override fun shouldNavigateToScanView(): Flow<Boolean> = natPreferencesDataSource.shouldNavigateToScanView()

  override fun scanViewSelectedTabIndex(): Flow<Int> = natPreferencesDataSource.scanViewSelectedTabIndex()

  override fun completeScanResult(): Flow<CompleteScanResult> = natPreferencesDataSource.completeScanResult()

  override fun showTagInfoScanResult(): Flow<ShowTagInfoScanResult> = natPreferencesDataSource.showTagInfoScanResult()
}
