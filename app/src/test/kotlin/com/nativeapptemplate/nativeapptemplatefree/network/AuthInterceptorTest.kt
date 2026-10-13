package com.nativeapptemplate.nativeapptemplatefree.network

import com.nativeapptemplate.nativeapptemplatefree.UserPreferences
import com.nativeapptemplate.nativeapptemplatefree.datastore.NativeAppTemplatePreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.datastoreTest.InMemoryDataStore
import com.sun.net.httpserver.HttpServer
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
import java.net.InetAddress
import java.net.InetSocketAddress

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
    val interceptor = AuthInterceptor(dataSource, API_HOST)
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
    val interceptor = AuthInterceptor(dataSource, API_HOST)
    val sent = interceptor.sentRequest(Request.Builder().url("https://example.com/").build())
    assertEquals("android", sent.header("source"))
    assertEquals("application/vnd.api+json; charset=utf-8", sent.header("Accept"))
    // A GET has no body, so no Content-Type (OkHttp sets it from the body when there is one).
    assertNull(sent.header("Content-Type"))
  }

  @Test
  fun intercept_requestWithABody_keepsASingleContentType() = testScope.runTest {
    val interceptor = AuthInterceptor(dataSourceWith(token = "t", client = "c", uid = "u", expiry = "1"), API_HOST)
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
    val interceptor = AuthInterceptor(dataSource, API_HOST)
    val sent = interceptor.sentRequest(Request.Builder().url("https://example.com/").build())
    assertNull(sent.header("access-token"))
    assertNull(sent.header("token-type"))
    assertNull(sent.header("client"))
    assertNull(sent.header("expiry"))
    assertNull(sent.header("uid"))
    assertEquals("android", sent.header("source"))
  }

  @Test
  fun intercept_requestToAnotherHost_omitsAllSessionHeaders() = testScope.runTest {
    // A redirect from the API to another host runs through this network interceptor again, so the
    // session must stay with the API host. OkHttp itself only strips `Authorization` on such hops.
    val interceptor = AuthInterceptor(
      dataSourceWith(token = "test-token", client = "test-client", uid = "john@example.com", expiry = "12345"),
      API_HOST,
    )

    val sent = interceptor.sentRequest(Request.Builder().url("https://attacker.example.net/collect").build())

    assertNull(sent.header("access-token"))
    assertNull(sent.header("token-type"))
    assertNull(sent.header("client"))
    assertNull(sent.header("expiry"))
    assertNull(sent.header("uid"))
  }

  @Test
  fun intercept_redirectFromTheApiToAnotherHost_doesNotForwardTheSession() {
    // End to end through OkHttp's redirect handling: the "API" on 127.0.0.1 answers 302 to a
    // server reached as "localhost" (a different host name, so OkHttp treats it as another host).
    val loopback = InetAddress.getLoopbackAddress()
    var tokenSeenByOtherHost: String? = "not called"
    val otherHost = HttpServer.create(InetSocketAddress(loopback, 0), 0).apply {
      createContext("/") { exchange ->
        tokenSeenByOtherHost = exchange.requestHeaders.getFirst("access-token")
        exchange.sendResponseHeaders(204, -1)
        exchange.close()
      }
      start()
    }
    val api = HttpServer.create(InetSocketAddress(loopback, 0), 0).apply {
      createContext("/") { exchange ->
        exchange.responseHeaders.add("Location", "http://localhost:${otherHost.address.port}/collect")
        exchange.sendResponseHeaders(302, -1)
        exchange.close()
      }
      start()
    }
    try {
      val interceptor = AuthInterceptor(
        dataSourceWith(token = "test-token", client = "test-client", uid = "john@example.com", expiry = "12345"),
        apiHost = "127.0.0.1",
      )
      OkHttpClient.Builder()
        .addNetworkInterceptor(interceptor)
        .build()
        .newCall(Request.Builder().url("http://127.0.0.1:${api.address.port}/").build())
        .execute()
        .close()

      // The other host was reached (so the redirect was followed) but got no token.
      assertNull(tokenSeenByOtherHost)
    } finally {
      api.stop(0)
      otherHost.stop(0)
    }
  }

  @Test
  fun intercept_subdomainOfTheApiHost_isAnotherHost() = testScope.runTest {
    // Exact host match: "evil.example.com" must not count as "example.com".
    val interceptor = AuthInterceptor(
      dataSourceWith(token = "test-token", client = "test-client", uid = "john@example.com", expiry = "12345"),
      API_HOST,
    )

    val sent = interceptor.sentRequest(Request.Builder().url("https://evil.$API_HOST/").build())

    assertNull(sent.header("access-token"))
  }

  @Test
  fun intercept_apiHostConfiguredInAnotherCase_isStillTheApiHost() = testScope.runTest {
    // Host names are case-insensitive (RFC 4343), and OkHttp lowercases the request's host, so a
    // NATIVEAPPTEMPLATE_API_DOMAIN written with capitals must still match.
    val interceptor = AuthInterceptor(
      dataSourceWith(token = "test-token", client = "test-client", uid = "john@example.com", expiry = "12345"),
      apiHost = "Example.COM",
    )

    val sent = interceptor.sentRequest(Request.Builder().url("https://example.com/").build())

    assertEquals("test-token", sent.header("access-token"))
  }

  @Test
  fun intercept_preservesOriginalRequestUrl() = testScope.runTest {
    val dataSource = NativeAppTemplatePreferencesDataSource(
      InMemoryDataStore(UserPreferences.getDefaultInstance()),
    )
    val interceptor = AuthInterceptor(dataSource, API_HOST)
    val originalUrl = "https://example.com/path?query=value"
    val sent = interceptor.sentRequest(Request.Builder().url(originalUrl).build())

    assertEquals(originalUrl, sent.url.toString())
  }
}

/** The API host the interceptor is built for in these tests; requests to it carry the session. */
private const val API_HOST = "example.com"

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
