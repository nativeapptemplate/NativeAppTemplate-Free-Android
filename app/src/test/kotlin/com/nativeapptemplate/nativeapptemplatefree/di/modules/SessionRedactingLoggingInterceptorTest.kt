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
          .header("Authorization", RESPONSE_BEARER)
          .header("Set-Cookie", RESPONSE_COOKIE)
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

    listOf(REQUEST_TOKEN, REQUEST_CLIENT, RESPONSE_TOKEN, RESPONSE_CLIENT, UID, EXPIRY, RESPONSE_BEARER_TOKEN, RESPONSE_COOKIE_VALUE).forEach { secret ->
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
    assertTrue("Authorization not logged as redacted:\n$logged", logged.contains("Authorization: ██"))
    assertTrue("Set-Cookie not logged as redacted:\n$logged", logged.contains("Set-Cookie: ██"))
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

/**
 * devise_token_auth (1.2+, DeviseTokenAuth::Concerns::User#build_bearer_token) also returns the
 * whole session as `Authorization: Bearer <Base64 of the header JSON>`, enough on its own to sign in
 * as the shopkeeper. This is Base64 of
 * {"access-token":"response-token-abcd","token-type":"Bearer","client":"response-client-efgh","expiry":"1767225600","uid":"john@example.com"}.
 */
private const val RESPONSE_BEARER_TOKEN =
  "eyJhY2Nlc3MtdG9rZW4iOiJyZXNwb25zZS10b2tlbi1hYmNkIiwidG9rZW4tdHlwZSI6IkJlYXJlciIsImNsaWVudCI6InJlc3BvbnNlLWNsaWVudC1lZmdoIiwiZXhwaXJ5IjoiMTc2NzIyNTYwMCIsInVpZCI6ImpvaG5AZXhhbXBsZS5jb20ifQ=="
private const val RESPONSE_BEARER = "Bearer $RESPONSE_BEARER_TOKEN"

/** Rails sends its session cookie with every API response; the app never uses it. */
private const val RESPONSE_COOKIE_VALUE = "cookie-value-9876"
private const val RESPONSE_COOKIE = "_nativeapptemplateapi_session=$RESPONSE_COOKIE_VALUE; path=/; httponly; samesite=lax"
