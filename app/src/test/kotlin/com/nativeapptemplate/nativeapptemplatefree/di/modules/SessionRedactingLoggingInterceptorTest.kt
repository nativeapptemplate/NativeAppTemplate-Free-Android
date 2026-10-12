package com.nativeapptemplate.nativeapptemplatefree.di.modules

import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionRedactingLoggingInterceptorTest {
  private val logged = mutableListOf<String>()

  /** Logs one call whose request and response both carry the session headers. */
  private fun logOneCall() {
    val client = OkHttpClient.Builder()
      .addInterceptor(sessionRedactingLoggingInterceptor(HttpLoggingInterceptor.Level.HEADERS) { logged += it })
      // Answers in place of the network, with the headers the API returns on sign-in.
      .addInterceptor { chain ->
        Response.Builder()
          .request(chain.request())
          .protocol(Protocol.HTTP_1_1)
          .code(200)
          .message("OK")
          .header("access-token", RESPONSE_TOKEN)
          .header("client", RESPONSE_CLIENT)
          .header("uid", UID)
          .header("expiry", EXPIRY)
          .header("token-type", "Bearer")
          .body("{}".toResponseBody())
          .build()
      }
      .build()

    val request = Request.Builder()
      .url("https://api.example.com/shopkeeper_auth")
      .header("access-token", REQUEST_TOKEN)
      .header("client", REQUEST_CLIENT)
      .header("uid", UID)
      .header("expiry", EXPIRY)
      .build()
    client.newCall(request).execute().close()
  }

  @Test
  fun sessionHeaderValues_neverReachTheLog() {
    logOneCall()
    val log = logged.joinToString("\n")

    listOf(REQUEST_TOKEN, REQUEST_CLIENT, RESPONSE_TOKEN, RESPONSE_CLIENT, UID, EXPIRY).forEach { secret ->
      assertFalse("$secret was logged:\n$log", secret in log)
    }
  }

  @Test
  fun sessionHeaders_areStillListed_withTheirValueRedacted() {
    logOneCall()

    // HttpLoggingInterceptor writes a redacted header as "name: ██" (okhttp3.logging, logHeader).
    listOf("access-token", "client", "uid", "expiry").forEach { name ->
      assertTrue("$name not logged as redacted:\n$logged", logged.count { it == "$name: ██" } == 2)
    }
    // Other headers keep their value.
    assertTrue(logged.contains("token-type: Bearer"))
  }
}

private const val REQUEST_TOKEN = "request-token-1234"
private const val REQUEST_CLIENT = "request-client-5678"
private const val RESPONSE_TOKEN = "response-token-abcd"
private const val RESPONSE_CLIENT = "response-client-efgh"
private const val UID = "john@example.com"
private const val EXPIRY = "1767225600"
