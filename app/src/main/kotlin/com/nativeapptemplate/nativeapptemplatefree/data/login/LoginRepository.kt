package com.nativeapptemplate.nativeapptemplatefree.data.login

import com.nativeapptemplate.nativeapptemplatefree.model.*
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Login
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
  val userData: Flow<UserData>

  fun login(
    login: Login,
  ): Flow<LoggedInShopkeeper>

  /**
   * Signs out on the server, then [clearSession] with [notice], whether or not the request
   * succeeded.
   */
  fun logout(notice: SignOutNotice? = null): Flow<Boolean>

  fun getPermissions(): Flow<Permissions>

  fun updateConfirmedPrivacyVersion(): Flow<Boolean>

  fun updateConfirmedTermsVersion(): Flow<Boolean>

  suspend fun setAccountId(accountId: String)

  suspend fun setShopkeeper(loggedInShopkeeper: LoggedInShopkeeper)

  suspend fun setShopkeeperForUpdate(loggedInShopkeeper: LoggedInShopkeeper)

  suspend fun setPermissions(permissions: Permissions)

  suspend fun setDidShowTapShopBelowTip(didShowTapShopBelowTip: Boolean)

  suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

  suspend fun setIsEmailUpdated(isEmailUpdated: Boolean)

  suspend fun setIsMyAccountDeleted(isMyAccountDeleted: Boolean)

  suspend fun setIsShopDeleted(isShopDeleted: Boolean)

  /**
   * Forgets the signed-in shopkeeper (session, account, permissions, one-shot flags) and keeps the
   * device settings (dark theme, tips already shown).
   *
   * [notice] is stored in the same write that clears the session, so the signed-out screen shows
   * it even though the calling screen is gone by the time this returns.
   */
  suspend fun clearSession(notice: SignOutNotice? = null)

  fun isLoggedIn(): Flow<Boolean>

  fun shouldUpdateApp(): Flow<Boolean>

  fun shouldUpdatePrivacy(): Flow<Boolean>

  fun shouldUpdateTerms(): Flow<Boolean>

  fun isEmailUpdated(): Flow<Boolean>

  fun isMyAccountDeleted(): Flow<Boolean>

  fun isShopDeleted(): Flow<Boolean>

  fun didShowTapShopBelowTip(): Flow<Boolean>
}
