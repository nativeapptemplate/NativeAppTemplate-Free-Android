package com.nativeapptemplate.nativeapptemplatefree.data.login

import com.nativeapptemplate.nativeapptemplatefree.model.*
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Login
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
  val userData: Flow<UserData>

  fun login(
    login: Login
  ): Flow<LoggedInShopkeeper>

  fun logout(): Flow<Boolean>

  fun getPermissions(): Flow<Permissions>

  fun updateConfirmedPrivacyVersion(
  ): Flow<Boolean>

  fun updateConfirmedTermsVersion(
  ): Flow<Boolean>

  suspend fun setShouldFetchItemTagForShowTagInfoScan(shouldFetchItemTagForShowTagInfoScan: Boolean)

  suspend fun setShouldCompleteItemTagForCompleteScan(shouldCompleteItemTagForCompleteScan: Boolean)

  suspend fun setShouldNavigateToScanView(shouldNavigateToScanView: Boolean)

  suspend fun setScanViewSelectedTabIndex(scanViewSelectedTabIndex: Int)

  suspend fun setCompleteScanResult(completeScanResult: CompleteScanResult)

  suspend fun setShowTagInfoScanResult(showTagInfoScanResult: ShowTagInfoScanResult)

  suspend fun setAccountId(accountId: String)

  suspend fun setShopkeeper(loggedInShopkeeper: LoggedInShopkeeper)

  suspend fun setShopkeeperForUpdate(loggedInShopkeeper: LoggedInShopkeeper)

  suspend fun setPermissions(permissions: Permissions)

  suspend fun setDidShowTapShopBelowTip(didShowTapShopBelowTip: Boolean)

  suspend fun setDidShowReadInstructionsTip(didShowReadInstructionsTip: Boolean)

  suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

  suspend fun setIsEmailUpdated(isEmailUpdated: Boolean)

  suspend fun setIsMyAccountDeleted(isMyAccountDeleted: Boolean)

  suspend fun setIsShopDeleted(isShopDeleted: Boolean)

  suspend fun clearUserPreferences()

  fun isLoggedIn(): Flow<Boolean>

  fun shouldUpdateApp(): Flow<Boolean>

  fun shouldUpdatePrivacy(): Flow<Boolean>

  fun shouldUpdateTerms(): Flow<Boolean>

  fun isEmailUpdated(): Flow<Boolean>

  fun isMyAccountDeleted(): Flow<Boolean>

  fun isShopDeleted(): Flow<Boolean>

  fun didShowTapShopBelowTip(): Flow<Boolean>

  fun didShowReadInstructionsTip(): Flow<Boolean>

  fun getMaximumQueueNumberLength(): Flow<Int>

  fun shouldFetchItemTagForShowTagInfoScan(): Flow<Boolean>

  fun shouldCompleteItemTagForCompleteScan(): Flow<Boolean>

  fun shouldNavigateToScanView(): Flow<Boolean>

  fun scanViewSelectedTabIndex(): Flow<Int>

  fun completeScanResult(): Flow<CompleteScanResult>

  fun showTagInfoScanResult(): Flow<ShowTagInfoScanResult>
}

