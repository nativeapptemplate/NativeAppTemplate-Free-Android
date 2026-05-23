package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ItemTagBodyDetail(
  val name: String,
  // No default: with the Json config's encodeDefaults = false, a value equal to
  // its default ("") is dropped from the payload, so clearing a description on
  // edit would be silently ignored by the server's partial update. Both call
  // sites supply description explicitly.
  val description: String,
) : Parcelable
