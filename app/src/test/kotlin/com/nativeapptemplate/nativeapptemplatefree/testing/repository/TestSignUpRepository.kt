package com.nativeapptemplate.nativeapptemplatefree.testing.repository

import com.nativeapptemplate.nativeapptemplatefree.data.login.SignUpRepository
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.SendConfirmation
import com.nativeapptemplate.nativeapptemplatefree.model.SendResetPassword
import com.nativeapptemplate.nativeapptemplatefree.model.SignUp
import com.nativeapptemplate.nativeapptemplatefree.model.SignUpForUpdate
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class TestSignUpRepository : SignUpRepository {
  private val loggedInShopkeeperFlow: MutableSharedFlow<LoggedInShopkeeper> =
    MutableSharedFlow(replay = 1, onBufferOverflow = DROP_OLDEST)

  /** The last request passed to [signUp]. */
  var lastSignUp: SignUp? = null
    private set

  override fun signUp(signUp: SignUp): Flow<LoggedInShopkeeper> {
    lastSignUp = signUp
    return loggedInShopkeeperFlow
  }

  /** The last request passed to [updateAccount]. */
  var lastSignUpForUpdate: SignUpForUpdate? = null
    private set

  override fun updateAccount(
    signUpForUpdate: SignUpForUpdate,
  ): Flow<LoggedInShopkeeper> {
    lastSignUpForUpdate = signUpForUpdate
    return loggedInShopkeeperFlow
  }

  /** Every locale passed to [updateLocale], in order. */
  val updatedLocales = mutableListOf<String>()

  private var updateLocaleError: Throwable? = null

  override fun updateLocale(locale: String): Flow<LoggedInShopkeeper> {
    updatedLocales += locale
    return updateLocaleError?.let { error -> flow { throw error } } ?: loggedInShopkeeperFlow
  }

  /** Makes every later [updateLocale] call fail with [error]. */
  fun failUpdateLocale(error: Throwable) {
    updateLocaleError = error
  }

  private var deleteAccountError: Throwable? = null

  override fun deleteAccount(): Flow<Boolean> = deleteAccountError?.let { error -> flow { throw error } } ?: MutableStateFlow(true)

  /** Makes every later [deleteAccount] call fail with [error]. */
  fun failDeleteAccount(error: Throwable) {
    deleteAccountError = error
  }

  override fun sendResetPasswordInstruction(
    sendResetPassword: SendResetPassword,
  ): Flow<Boolean> = MutableStateFlow(true)

  override fun sendConfirmationInstruction(
    sendConfirmation: SendConfirmation,
  ): Flow<Boolean> = MutableStateFlow(true)

  /**
   * A test-only API.
   */
  fun sendLoggedInShopkeeper(loggedInShopkeeper: LoggedInShopkeeper) {
    loggedInShopkeeperFlow.tryEmit(loggedInShopkeeper)
  }
}
