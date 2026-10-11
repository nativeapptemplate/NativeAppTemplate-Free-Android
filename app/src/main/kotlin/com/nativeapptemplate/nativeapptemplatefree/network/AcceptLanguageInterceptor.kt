package com.nativeapptemplate.nativeapptemplatefree.network

import androidx.core.os.LocaleListCompat
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sends the device's preferred languages as Accept-Language. OkHttp sends none by default, and the
 * API answers requests made before sign-in (sign-up, sign-in errors, password reset) in that language.
 * A signed-in shopkeeper's responses use the locale saved on the server instead.
 */
@Singleton
class AcceptLanguageInterceptor(
  private val languageTags: () -> List<String>,
) : Interceptor {
  @Inject
  constructor() : this(::deviceLanguageTags)

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    if (request.header(ACCEPT_LANGUAGE) != null) return chain.proceed(request)

    val value = acceptLanguageHeader(languageTags())
      ?: return chain.proceed(request)

    return chain.proceed(request.newBuilder().header(ACCEPT_LANGUAGE, value).build())
  }

  companion object {
    const val ACCEPT_LANGUAGE = "Accept-Language"
  }
}

/** The most preferred tag first, the rest with q decreasing by 0.1 (lowest 0.1); null when there is none. */
internal fun acceptLanguageHeader(languageTags: List<String>): String? {
  val tags = languageTags
    .filter { it.isNotBlank() && it != UNDETERMINED_LANGUAGE }
    .distinct()
    .take(MAX_LANGUAGES)
  if (tags.isEmpty()) return null

  return tags.mapIndexed { index, tag ->
    if (index == 0) tag else "$tag;q=0.${MAX_LANGUAGES - index}"
  }.joinToString(", ")
}

// q = 0.9 ... 0.1 after the first tag.
private const val MAX_LANGUAGES = 10

// Locale.ROOT's tag.
private const val UNDETERMINED_LANGUAGE = "und"

private fun deviceLanguageTags(): List<String> {
  val locales = LocaleListCompat.getAdjustedDefault()
  val tags = (0 until locales.size()).mapNotNull { locales[it]?.toLanguageTag() }
  return tags.ifEmpty { listOf(Locale.getDefault().toLanguageTag()) }
}
