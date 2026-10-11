package com.nativeapptemplate.nativeapptemplatefree.model

import kotlinx.serialization.Serializable

/** Body of `PATCH /shopkeeper_auth` that changes only the shopkeeper's locale. */
@Serializable
data class ShopkeeperLocaleUpdate(
  // "en" or "ja" (Locales.supported). No default, so it is always in the payload.
  val locale: String,
)
