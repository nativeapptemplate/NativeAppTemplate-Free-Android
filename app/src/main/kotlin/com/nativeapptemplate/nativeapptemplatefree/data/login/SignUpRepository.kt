package com.nativeapptemplate.nativeapptemplatefree.data.login

import com.nativeapptemplate.nativeapptemplatefree.model.*
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import kotlinx.coroutines.flow.Flow

interface SignUpRepository {
  fun signUp(
    signUp: SignUp,
  ): Flow<LoggedInShopkeeper>

  fun updateAccount(
    signUpForUpdate: SignUpForUpdate,
  ): Flow<LoggedInShopkeeper>

  /** Changes only `shopkeepers.locale` ("en" or "ja"). */
  fun updateLocale(
    locale: String,
  ): Flow<LoggedInShopkeeper>

  fun deleteAccount(): Flow<Boolean>

  fun sendResetPasswordInstruction(
    sendResetPassword: SendResetPassword,
  ): Flow<Boolean>

  fun sendConfirmationInstruction(
    sendConfirmation: SendConfirmation,
  ): Flow<Boolean>
}
