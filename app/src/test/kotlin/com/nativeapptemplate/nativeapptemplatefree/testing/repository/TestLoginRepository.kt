package com.nativeapptemplate.nativeapptemplatefree.testing.repository

import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.model.CompleteScanResult
import com.nativeapptemplate.nativeapptemplatefree.model.DarkThemeConfig
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Login
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import com.nativeapptemplate.nativeapptemplatefree.model.ShowTagInfoScanResult
import com.nativeapptemplate.nativeapptemplatefree.model.UserData
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

val emptyUserData = UserData(
)

class TestLoginRepository : LoginRepository {
  private val loggedInShopkeeperFlow: MutableSharedFlow<LoggedInShopkeeper> =
    MutableSharedFlow(replay = 1, onBufferOverflow = DROP_OLDEST)

  private val permissionsFlow: MutableSharedFlow<Permissions> =
    MutableSharedFlow(replay = 1, onBufferOverflow = DROP_OLDEST)

  private val isLoggedInReturnFlow: MutableSharedFlow<Boolean> =
    MutableSharedFlow(replay = 1, onBufferOverflow = DROP_OLDEST)

  private val _userData = MutableSharedFlow<UserData>(replay = 1, onBufferOverflow = DROP_OLDEST)

  private val currentUserData get() = _userData.replayCache.firstOrNull() ?: emptyUserData

  override val userData: Flow<UserData> = _userData.filterNotNull()

  override fun login(login: Login): Flow<LoggedInShopkeeper> = loggedInShopkeeperFlow

  override fun logout(): Flow<Boolean> =  MutableStateFlow(true)

  override fun getPermissions(): Flow<Permissions> = permissionsFlow

  override fun updateConfirmedPrivacyVersion(): Flow<Boolean> =  MutableStateFlow(true)

  override fun updateConfirmedTermsVersion(): Flow<Boolean> =  MutableStateFlow(true)

  override suspend fun setShouldFetchItemTagForShowTagInfoScan(shouldFetchItemTagForShowTagInfoScan: Boolean) {
    currentUserData.let { current ->
      _userData.tryEmit(current.copy(shouldFetchItemTagForShowTagInfoScan = shouldFetchItemTagForShowTagInfoScan))
    }
  }

  override suspend fun setShouldCompleteItemTagForCompleteScan(shouldCompleteItemTagForCompleteScan: Boolean) {
    currentUserData.let { current ->
      _userData.tryEmit(current.copy(shouldCompleteItemTagForCompleteScan = shouldCompleteItemTagForCompleteScan))
    }
  }

  override suspend fun setShouldNavigateToScanView(shouldNavigateToScanView: Boolean) {
  }

  override suspend fun setScanViewSelectedTabIndex(scanViewSelectedTabIndex: Int) {
    currentUserData.let { current ->
      _userData.tryEmit(current.copy(scanViewSelectedTabIndex = scanViewSelectedTabIndex))
    }
  }

  override suspend fun setCompleteScanResult(completeScanResult: CompleteScanResult) {
  }

  override suspend fun setShowTagInfoScanResult(showTagInfoScanResult: ShowTagInfoScanResult) {
  }

  override suspend fun setAccountId(accountId: String) {
  }

  override suspend fun setShopkeeper(loggedInShopkeeper: LoggedInShopkeeper) {
    currentUserData.let { current ->
      _userData.tryEmit(
        current.copy(
          id = loggedInShopkeeper.getId()!!,
          accountId = loggedInShopkeeper.getAccountId()!!,
          personalAccountId = loggedInShopkeeper.getPersonalAccountId()!!,
          accountOwnerId = loggedInShopkeeper.getAccountOwnerId()!!,
          accountName = loggedInShopkeeper.getAccountName()!!,
          email = loggedInShopkeeper.getEmail()!!,
          name = loggedInShopkeeper.getName()!!,
          timeZone = loggedInShopkeeper.getTimeZone()!!,
          token = loggedInShopkeeper.getToken()!!,
          client = loggedInShopkeeper.getClient()!!,
          uid = loggedInShopkeeper.getUID()!!,
          expiry = loggedInShopkeeper.getExpiry()!!,
          isLoggedIn = true,
        )
      )
    }
  }

  override suspend fun setShopkeeperForUpdate(loggedInShopkeeper: LoggedInShopkeeper) {
    currentUserData.let { current ->
      _userData.tryEmit(
        current.copy(
          email = loggedInShopkeeper.getEmail()!!,
          name = loggedInShopkeeper.getName()!!,
          timeZone = loggedInShopkeeper.getTimeZone()!!,
          uid = loggedInShopkeeper.getUID()!!,
        )
      )
    }
  }
  override suspend fun setPermissions(permissions: Permissions) {
    currentUserData.let { current ->
      _userData.tryEmit(
        current.copy(
          androidAppVersion =  permissions.getAndroidAppVersion()!!,
          shouldUpdatePrivacy =  permissions.getShouldUpdatePrivacy()!!,
          shouldUpdateTerms = permissions.getShouldUpdateTerms()!!,
          shopLimitCount = permissions.getShopLimitCount()!!,
        )
      )
    }
  }

  override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
    currentUserData.let { current ->
      _userData.tryEmit(current.copy(darkThemeConfig = darkThemeConfig))
    }
  }

  override suspend fun setDidShowTapShopBelowTip(didShowTapShopBelowTip: Boolean) {
  }

  override suspend fun setDidShowReadInstructionsTip(didShowReadInstructionsTip: Boolean) {
    currentUserData.let { current ->
      _userData.tryEmit(current.copy(didShowReadInstructionsTip = didShowReadInstructionsTip))
    }
  }

  override suspend fun setIsEmailUpdated(isEmailUpdated: Boolean) {
    currentUserData.let { current ->
      _userData.tryEmit(current.copy(isEmailUpdated = isEmailUpdated))
    }
  }

  override suspend fun setIsMyAccountDeleted(isMyAccountDeleted: Boolean) {
    currentUserData.let { current ->
      _userData.tryEmit(current.copy(isMyAccountDeleted = isMyAccountDeleted))
    }
  }

  override suspend fun setIsShopDeleted(isShopDeleted: Boolean) {
  }

  override suspend fun clearUserPreferences() {
  }

  override fun isLoggedIn(): Flow<Boolean> = isLoggedInReturnFlow

  override fun shouldUpdateApp(): Flow<Boolean> = MutableStateFlow(false)

  override fun shouldUpdatePrivacy(): Flow<Boolean> = MutableStateFlow(false)

  override fun shouldUpdateTerms(): Flow<Boolean> = MutableStateFlow(false)

  override fun isEmailUpdated(): Flow<Boolean> = MutableStateFlow(false)

  override fun isMyAccountDeleted(): Flow<Boolean> = MutableStateFlow(false)

  override fun isShopDeleted(): Flow<Boolean> = MutableStateFlow(false)


  override fun didShowTapShopBelowTip(): Flow<Boolean> = MutableStateFlow(true)

  override fun didShowReadInstructionsTip(): Flow<Boolean> = userData.map { it.didShowReadInstructionsTip }

  override fun getMaximumQueueNumberLength(): Flow<Int> = userData.map { it.maximumQueueNumberLength }

  override fun shouldFetchItemTagForShowTagInfoScan(): Flow<Boolean> = MutableStateFlow(true)

  override fun shouldCompleteItemTagForCompleteScan(): Flow<Boolean> = MutableStateFlow(true)

  override fun shouldNavigateToScanView(): Flow<Boolean> = MutableStateFlow(true)

  override fun scanViewSelectedTabIndex(): Flow<Int> = MutableStateFlow(0)

  override fun completeScanResult(): Flow<CompleteScanResult> = MutableStateFlow(CompleteScanResult())

  override fun showTagInfoScanResult(): Flow<ShowTagInfoScanResult> = MutableStateFlow(ShowTagInfoScanResult())

  /**
   * A test-only API.
   */
  fun sendUserData(userData: UserData) {
    _userData.tryEmit(userData)
  }

  fun sendLoggedInShopkeeper(loggedInShopkeeper: LoggedInShopkeeper) {
    loggedInShopkeeperFlow.tryEmit(loggedInShopkeeper)
  }

  fun sendPermissions(permissions: Permissions) {
    permissionsFlow.tryEmit(permissions)
  }
}