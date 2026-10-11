package com.nativeapptemplate.nativeapptemplatefree.network

import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AcceptLanguageInterceptorTest {
  @Test
  fun intercept_sendsThePreferredLanguagesInOrder() {
    val interceptor = AcceptLanguageInterceptor { listOf("ja-JP", "en-US") }

    val sent = interceptor.sentRequest(Request.Builder().url("https://example.com/").build())

    // RFC 9110 §12.5.4: the first tag has the implied q=1; the second gets q=0.9.
    assertEquals("ja-JP, en-US;q=0.9", sent.header("Accept-Language"))
  }

  @Test
  fun intercept_keepsAnAcceptLanguageTheRequestAlreadyHas() {
    val interceptor = AcceptLanguageInterceptor { listOf("ja-JP") }
    val request = Request.Builder().url("https://example.com/").header("Accept-Language", "fr").build()

    val sent = interceptor.sentRequest(request)

    assertEquals(listOf("fr"), sent.headers("Accept-Language"))
  }

  @Test
  fun intercept_withoutAnyLanguage_sendsNoHeader() {
    val interceptor = AcceptLanguageInterceptor { emptyList() }

    val sent = interceptor.sentRequest(Request.Builder().url("https://example.com/").build())

    assertNull(sent.header("Accept-Language"))
  }

  @Test
  fun acceptLanguageHeader_dropsUndeterminedAndDuplicateTags() {
    // "und" is Locale.ROOT's tag, which names no language.
    assertEquals("en, ja;q=0.9", acceptLanguageHeader(listOf("en", "und", "en", "", "ja")))
  }

  @Test
  fun acceptLanguageHeader_stopsAtTheLowestQuality() {
    val tags = (1..12).map { "x-$it" }

    val header = acceptLanguageHeader(tags)!!

    // 1 tag without q, then q=0.9 down to q=0.1: 10 tags; the 11th and 12th are dropped.
    assertEquals(10, header.split(", ").size)
    assert(header.endsWith("x-10;q=0.1")) { header }
  }
}

/** Runs [request] through a real OkHttp chain and returns it as it left the interceptor. */
private fun AcceptLanguageInterceptor.sentRequest(request: Request): Request {
  var sent: Request? = null
  OkHttpClient.Builder()
    .addInterceptor(this)
    .addInterceptor { chain ->
      sent = chain.request()
      Response.Builder()
        .request(chain.request())
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK")
        .body("".toResponseBody(null))
        .build()
    }
    .build()
    .newCall(request)
    .execute()
    .close()
  return sent!!
}
