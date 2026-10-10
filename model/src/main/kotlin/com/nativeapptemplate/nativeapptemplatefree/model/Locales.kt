package com.nativeapptemplate.nativeapptemplatefree.model

/** The languages the API answers in, as stored in `shopkeepers.locale`. */
object Locales {
  const val ENGLISH = "en"
  const val JAPANESE = "ja"

  /** The server's fallback, and the value for a session saved before the app stored a locale. */
  const val DEFAULT = ENGLISH

  val supported = listOf(ENGLISH, JAPANESE)
}
