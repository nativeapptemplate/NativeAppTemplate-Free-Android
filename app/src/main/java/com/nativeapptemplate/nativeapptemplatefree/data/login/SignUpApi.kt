package com.nativeapptemplate.nativeapptemplatefree.data.login

import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.SendConfirmation
import com.nativeapptemplate.nativeapptemplatefree.model.SendResetPassword
import com.nativeapptemplate.nativeapptemplatefree.model.SignUp
import com.nativeapptemplate.nativeapptemplatefree.model.SignUpForUpdate
import com.nativeapptemplate.nativeapptemplatefree.model.Status
import com.skydoves.sandwich.ApiResponse
import retrofit2.Retrofit
import retrofit2.http.*

interface SignUpApi {
  @POST("shopkeeper_auth")
  suspend fun signUp(
    @Body data: SignUp
  ): ApiResponse<LoggedInShopkeeper>

  @PATCH("shopkeeper_auth")
  suspend fun updateAccount(
    @Body data: SignUpForUpdate
  ): ApiResponse<LoggedInShopkeeper>

  @DELETE("shopkeeper_auth")
  suspend fun deleteAccount(
  ): ApiResponse<Status>

  @POST("shopkeeper_auth/password")
  suspend fun sendResetPasswordInstruction(
    @Body data: SendResetPassword
  ): ApiResponse<Status>

  @POST("shopkeeper_auth/confirmation")
  suspend fun sendConfirmationInstruction(
    @Body data: SendConfirmation
  ): ApiResponse<Status>

  companion object {
    fun create(retroFit: Retrofit): SignUpApi = retroFit.create(
      SignUpApi::class.java
    )
  }
}
