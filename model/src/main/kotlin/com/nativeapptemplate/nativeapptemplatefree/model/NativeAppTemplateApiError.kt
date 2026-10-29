package com.nativeapptemplate.nativeapptemplatefree.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NativeAppTemplateApiError(
  val code: Int,

  @SerialName("error_message")
  val message: String,

  /** Only some errors carry it, e.g. "account_not_found" (see [ApiException.isAccountGone] in the app). */
  @SerialName("error_code")
  val errorCode: String? = null,
)
