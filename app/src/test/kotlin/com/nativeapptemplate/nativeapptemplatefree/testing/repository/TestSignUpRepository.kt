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

  override fun signUp(signUp: SignUp): Flow<LoggedInShopkeeper> = loggedInShopkeeperFlow

  override fun updateAccount(
    signUpForUpdate: SignUpForUpdate,
  ): Flow<LoggedInShopkeeper> = loggedInShopkeeperFlow

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
