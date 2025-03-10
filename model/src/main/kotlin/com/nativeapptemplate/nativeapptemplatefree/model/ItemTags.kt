package com.nativeapptemplate.nativeapptemplatefree.model

import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

/**
 * Model for list response of Bookmark, Content, Progressions
 */
@Serializable
@Parcelize
data class ItemTags(
  @SerialName("data")
  val datum: List<Data> = emptyList(),
  val included: List<Data>? = null,
  val meta: Meta? = null
) : Parcelable {
  fun getDatumWithRelationships(): List<Data> = datum.map {
    it.updateRelationships(included)
  }
}
