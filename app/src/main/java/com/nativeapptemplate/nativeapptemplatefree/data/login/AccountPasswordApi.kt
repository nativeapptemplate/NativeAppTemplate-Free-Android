package com.nativeapptemplate.nativeapptemplatefree.data.login

import com.nativeapptemplate.nativeapptemplatefree.model.Status
import com.nativeapptemplate.nativeapptemplatefree.model.UpdatePasswordBody
import com.skydoves.sandwich.ApiResponse
import retrofit2.Retrofit
import retrofit2.http.*

interface AccountPasswordApi {
  @PATCH("{account_id}/api/v1/shopkeeper/account/password")
  suspend fun updateAccountPassword(
    @Path("account_id") accountId: String,
    @Body data: UpdatePasswordBody
  ): ApiResponse<Status>

  companion object {
    fun create(retroFit: Retrofit): AccountPasswordApi = retroFit.create(
      AccountPasswordApi::class.java
    )
  }
}
