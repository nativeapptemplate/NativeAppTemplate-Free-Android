package com.nativeapptemplate.nativeapptemplatefree.data.login

import android.util.Log
import com.nativeapptemplate.nativeapptemplatefree.common.errors.ApiException
import com.nativeapptemplate.nativeapptemplatefree.di.modules.ApplicationScope
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "SessionLifecycle"

/**
 * Starts, refreshes and ends the signed-in session.
 *
 * Each step runs in the application scope and the caller waits for it: the screens that call these
 * are removed by the very change they make (storing a session leaves the sign-in screen, clearing
 * it leaves Edit Profile), which cancels their viewModelScope while the step still waits on the
 * server. Cancelling the caller only stops the wait, not the step.
 */
@Singleton
class SessionLifecycle @Inject constructor(
  private val loginRepository: LoginRepository,
  private val appLanguageSyncer: AppLanguageSyncer,
  @param:ApplicationScope private val scope: CoroutineScope,
) {
  /**
   * Replaces any previous session with [loggedInShopkeeper], then brings it up to date: the
   * shopkeeper's locale in the background, and the permissions, which this waits for.
   *
   * Throws what [refreshPermissions] throws.
   */
  suspend fun start(loggedInShopkeeper: LoggedInShopkeeper) = inApplicationScope {
    loginRepository.clearSession()
    loginRepository.setShopkeeper(loggedInShopkeeper)
    // The account may have been created, or last used, in another language.
    scope.launch { appLanguageSyncer.sync() }
    fetchPermissions()
  }

  /**
   * Fetches and stores the permissions. On failure the cached permissions stay in use and the
   * failure is thrown; only a rejected session (401) or a deleted account
   * ([ApiException.isAccountGone]) signs the shopkeeper out first, as being offline or a server
   * error must not wipe the session.
   */
  suspend fun refreshPermissions() = inApplicationScope { fetchPermissions() }

  /**
   * Signs out on the server, then locally with [notice] (see [LoginRepository.logout]). The session
   * is cleared even when the request fails; that failure is then thrown.
   */
  suspend fun signOut(notice: SignOutNotice? = null) = inApplicationScope {
    loginRepository.logout(notice).collect { }
  }

  private suspend fun fetchPermissions() {
    val permissions = loginRepository.getPermissions()
      .catch { exception ->
        val apiException = exception as? ApiException
        if (apiException?.isUnauthorized == true || apiException?.isAccountGone == true) {
          loginRepository.logout()
            .catch { logoutException -> Log.e(TAG, "Logout error", logoutException) }
            .collect { }
        }
        throw exception
      }
      .first()
    loginRepository.setPermissions(permissions)
  }

  private suspend fun <T> inApplicationScope(block: suspend CoroutineScope.() -> T): T = scope.async(block = block).await()
}
