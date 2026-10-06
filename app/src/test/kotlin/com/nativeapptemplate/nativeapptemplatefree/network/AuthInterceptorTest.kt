package com.nativeapptemplate.nativeapptemplatefree.network

import com.nativeapptemplate.nativeapptemplatefree.UserPreferences
import com.nativeapptemplate.nativeapptemplatefree.datastore.NativeAppTemplatePreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.datastoreTest.InMemoryDataStore
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthInterceptorTest {
  private val testScope = TestScope(UnconfinedTestDispatcher())

  private fun dataSourceWith(
    token: String,
    client: String,
    uid: String,
    expiry: String,
  ): NativeAppTemplatePreferencesDataSource {
    val initial = UserPreferences.newBuilder()
      .setToken(token)
      .setClient(client)
      .setUid(uid)
      .setExpiry(expiry)
      .build()
    return NativeAppTemplatePreferencesDataSource(InMemoryDataStore(initial))
  }

  @Test
  fun intercept_withAuthData_addsAuthHeaders() = testScope.runTest {
    val dataSource = dataSourceWith(
      token = "test-token",
      client = "test-client",
      uid = "john@example.com",
      expiry = "12345",
    )
    val interceptor = AuthInterceptor(dataSource)
    val sent = interceptor.sentRequest(Request.Builder().url("https://example.com/").build())
    assertEquals("test-token", sent.header("access-token"))
    assertEquals("Bearer", sent.header("token-type"))
    assertEquals("test-client", sent.header("client"))
    assertEquals("12345", sent.header("expiry"))
    assertEquals("john@example.com", sent.header("uid"))
  }

  @Test
  fun intercept_withAuthData_addsBaseHeaders() = testScope.runTest {
    val dataSource = dataSourceWith(
      token = "test-token",
      client = "test-client",
      uid = "john@example.com",
      expiry = "12345",
    )
    val interceptor = AuthInterceptor(dataSource)
    val sent = interceptor.sentRequest(Request.Builder().url("https://example.com/").build())
    assertEquals("android", sent.header("source"))
    assertEquals("application/vnd.api+json; charset=utf-8", sent.header("Accept"))
    // A GET has no body, so no Content-Type (OkHttp sets it from the body when there is one).
    assertNull(sent.header("Content-Type"))
  }

  @Test
  fun intercept_requestWithABody_keepsASingleContentType() = testScope.runTest {
    val interceptor = AuthInterceptor(dataSourceWith(token = "t", client = "c", uid = "u", expiry = "1"))
    // As a network interceptor, AuthInterceptor runs after OkHttp's BridgeInterceptor has already set
    // Content-Type from the body.
    val request = Request.Builder()
      .url("https://example.com/")
      .post("{}".toRequestBody("application/json; charset=utf-8".toMediaType()))
      .header("Content-Type", "application/json; charset=utf-8")
      .build()

    val sent = interceptor.sentRequest(request)

    assertEquals(listOf("application/json; charset=utf-8"), sent.headers("Content-Type"))
  }

  @Test
  fun intercept_withoutAuthData_omitsAuthHeaders() = testScope.runTest {
    val dataSource = NativeAppTemplatePreferencesDataSource(
      InMemoryDataStore(UserPreferences.getDefaultInstance()),
    )
    val interceptor = AuthInterceptor(dataSource)
    val sent = interceptor.sentRequest(Request.Builder().url("https://example.com/").build())
    assertNull(sent.header("access-token"))
    assertNull(sent.header("token-type"))
    assertNull(sent.header("client"))
    assertNull(sent.header("expiry"))
    assertNull(sent.header("uid"))
    assertEquals("android", sent.header("source"))
  }

  @Test
  fun intercept_preservesOriginalRequestUrl() = testScope.runTest {
    val dataSource = NativeAppTemplatePreferencesDataSource(
      InMemoryDataStore(UserPreferences.getDefaultInstance()),
    )
    val interceptor = AuthInterceptor(dataSource)
    val originalUrl = "https://example.com/path?query=value"
    val sent = interceptor.sentRequest(Request.Builder().url(originalUrl).build())

    assertEquals(originalUrl, sent.url.toString())
  }
}

/**
 * Runs [request] through a real [OkHttpClient] with this interceptor, then short-circuits
 * with a canned response so no network I/O happens. Returns the request as it left the interceptor.
 */
private fun AuthInterceptor.sentRequest(request: Request): Request {
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
