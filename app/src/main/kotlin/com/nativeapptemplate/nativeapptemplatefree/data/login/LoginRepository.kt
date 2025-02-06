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

  suspend fun setAccountId(accountId: String)

  suspend fun setShopkeeper(loggedInShopkeeper: LoggedInShopkeeper)

  suspend fun setShopkeeperForUpdate(loggedInShopkeeper: LoggedInShopkeeper)

  suspend fun setPermissions(permissions: Permissions)

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
}

