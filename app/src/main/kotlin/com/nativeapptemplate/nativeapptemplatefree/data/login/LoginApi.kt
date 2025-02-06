package com.nativeapptemplate.nativeapptemplatefree.data.login

import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Login
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import com.nativeapptemplate.nativeapptemplatefree.model.Status
import com.skydoves.sandwich.ApiResponse
import retrofit2.Retrofit
import retrofit2.http.*

interface LoginApi {
  @POST("shopkeeper_auth/sign_in")
  suspend fun login(
    @Body data: Login
  ): ApiResponse<LoggedInShopkeeper>

  @DELETE("shopkeeper_auth/sign_out")
  suspend fun logout(): ApiResponse<Status>

  @GET("{account_id}/api/v1/shopkeeper/permissions")
  suspend fun getPermissions(
    @Path("account_id") accountId: String
  ): ApiResponse<Permissions>

  @PATCH("{account_id}/api/v1/shopkeeper/me/update_confirmed_privacy_version")
  suspend fun updateConfirmedPrivacyVersion(
    @Path("account_id") accountId: String
  ): ApiResponse<Status>

  @PATCH("{account_id}/api/v1/shopkeeper/me/update_confirmed_terms_version")
  suspend fun updateConfirmedTermsVersion(
    @Path("account_id") accountId: String
  ): ApiResponse<Status>

  companion object {
    fun create(retroFit: Retrofit): LoginApi = retroFit.create(
      LoginApi::class.java
    )
  }
}
