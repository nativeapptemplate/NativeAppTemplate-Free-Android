package com.nativeapptemplate.nativeapptemplatefree.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NativeAppTemplateApiError(
  val code: Int,

  @SerialName("error_message")
  val message: String
)