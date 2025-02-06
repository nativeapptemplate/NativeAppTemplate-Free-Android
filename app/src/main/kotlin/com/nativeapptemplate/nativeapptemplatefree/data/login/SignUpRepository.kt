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

  fun deleteAccount(
  ): Flow<Boolean>

  fun sendResetPasswordInstruction(
    sendResetPassword: SendResetPassword,
  ): Flow<Boolean>

  fun sendConfirmationInstruction(
    sendConfirmation: SendConfirmation,
  ): Flow<Boolean>
}
