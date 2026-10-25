package com.nativeapptemplate.nativeapptemplatefree.testing.repository

import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.data.login.SignOutNotice
import com.nativeapptemplate.nativeapptemplatefree.model.DarkThemeConfig
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Login
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import com.nativeapptemplate.nativeapptemplatefree.model.UserData
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow

val emptyUserData = UserData()

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

  private var loginError: Throwable? = null

  override fun login(login: Login): Flow<LoggedInShopkeeper> = loginError?.let { error -> flow { throw error } } ?: loggedInShopkeeperFlow

  /** How many times [logout] was collected. */
  var logoutCallCount = 0
    private set

  override fun logout(notice: SignOutNotice?): Flow<Boolean> = flow {
    logoutCallCount++
    onLogoutRequest()
    // LoginRepositoryImpl.logout clears the session whether or not the request succeeds.
    storeSignOutNotice(notice)
    onSessionCleared()
    emit(true)
  }

  /**
   * Runs once [logout] or [clearSession] has cleared the session. In the app, a cleared session
   * routes to onboarding, which removes the current screen and cancels its view model's scope while
   * the sign-out is still running. A test can do that here.
   */
  var onSessionCleared: suspend () -> Unit = {}

  /**
   * Runs while [logout] waits on the server, before the session is cleared. A test can close the
   * calling screen here, as the user can while the request is in flight.
   */
  var onLogoutRequest: suspend () -> Unit = {}

  private var permissionsError: Throwable? = null

  override fun getPermissions(): Flow<Permissions> = permissionsError?.let { error -> flow { throw error } } ?: permissionsFlow

  override fun updateConfirmedPrivacyVersion(): Flow<Boolean> = MutableStateFlow(true)

  override fun updateConfirmedTermsVersion(): Flow<Boolean> = MutableStateFlow(true)

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
          locale = loggedInShopkeeper.getLocale() ?: current.locale,
          token = loggedInShopkeeper.getToken()!!,
          client = loggedInShopkeeper.getClient()!!,
          uid = loggedInShopkeeper.getUID()!!,
          expiry = loggedInShopkeeper.getExpiry()!!,
          isLoggedIn = true,
        ),
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
          locale = loggedInShopkeeper.getLocale() ?: current.locale,
          uid = loggedInShopkeeper.getUID()!!,
        ),
      )
    }
  }
  override suspend fun setPermissions(permissions: Permissions) {
    currentUserData.let { current ->
      _userData.tryEmit(
        current.copy(
          androidAppVersion = permissions.getAndroidAppVersion()!!,
          shouldUpdatePrivacy = permissions.getShouldUpdatePrivacy()!!,
          shouldUpdateTerms = permissions.getShouldUpdateTerms()!!,
          shopLimitCount = permissions.getShopLimitCount()!!,
        ),
      )
    }
  }

  override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
    currentUserData.let { current ->
      _userData.tryEmit(current.copy(darkThemeConfig = darkThemeConfig))
    }
  }

  override suspend fun setDidShowTapShopBelowTip(didShowTapShopBelowTip: Boolean) {
    currentUserData.let { current ->
      _userData.tryEmit(current.copy(didShowTapShopBelowTip = didShowTapShopBelowTip))
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

  /** How many times [clearSession] was called. */
  var clearSessionCallCount = 0
    private set

  override suspend fun clearSession(notice: SignOutNotice?) {
    clearSessionCallCount++
    storeSignOutNotice(notice)
    onSessionCleared()
  }

  /** As the real session clear does: the notice is stored in the same write. */
  private fun storeSignOutNotice(notice: SignOutNotice?) {
    _userData.tryEmit(
      currentUserData.copy(
        isEmailUpdated = notice == SignOutNotice.EMAIL_UPDATED,
        isMyAccountDeleted = notice == SignOutNotice.ACCOUNT_DELETED,
      ),
    )
  }

  override fun isLoggedIn(): Flow<Boolean> = isLoggedInReturnFlow

  override fun shouldUpdateApp(): Flow<Boolean> = MutableStateFlow(false)

  override fun shouldUpdatePrivacy(): Flow<Boolean> = MutableStateFlow(false)

  override fun shouldUpdateTerms(): Flow<Boolean> = MutableStateFlow(false)

  override fun isEmailUpdated(): Flow<Boolean> = MutableStateFlow(false)

  override fun isMyAccountDeleted(): Flow<Boolean> = MutableStateFlow(false)

  private val _didShowTapShopBelowTip = MutableStateFlow(true)

  override fun didShowTapShopBelowTip(): Flow<Boolean> = _didShowTapShopBelowTip

  /** Collectors currently subscribed to this repository's never-completing flows. */
  val liveSubscriberCount: Int
    get() = _userData.subscriptionCount.value +
      _didShowTapShopBelowTip.subscriptionCount.value +
      isLoggedInReturnFlow.subscriptionCount.value

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

  /** Makes every later [login] call fail with [error], or succeed again when null. */
  fun failLogin(error: Throwable?) {
    loginError = error
  }

  /** Makes every later [getPermissions] call fail with [error]. */
  fun failPermissions(error: Throwable) {
    permissionsError = error
  }

  fun sendIsLoggedIn(isLoggedIn: Boolean) {
    isLoggedInReturnFlow.tryEmit(isLoggedIn)
  }
}
