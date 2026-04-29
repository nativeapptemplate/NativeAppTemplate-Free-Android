package com.nativeapptemplate.nativeapptemplatefree.network

import com.nativeapptemplate.nativeapptemplatefree.UserPreferences
import com.nativeapptemplate.nativeapptemplatefree.datastore.NatPreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.datastoreTest.InMemoryDataStore
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.Call
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.concurrent.TimeUnit

class AuthInterceptorTest {
  private val testScope = TestScope(UnconfinedTestDispatcher())

  private fun dataSourceWith(
    token: String,
    client: String,
    uid: String,
    expiry: String,
  ): NatPreferencesDataSource {
    val initial = UserPreferences.newBuilder()
      .setToken(token)
      .setClient(client)
      .setUid(uid)
      .setExpiry(expiry)
      .build()
    return NatPreferencesDataSource(InMemoryDataStore(initial))
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
    val chain = RecordingChain(Request.Builder().url("https://example.com/").build())

    interceptor.intercept(chain)

    val sent = chain.proceededRequest!!
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
    val chain = RecordingChain(Request.Builder().url("https://example.com/").build())

    interceptor.intercept(chain)

    val sent = chain.proceededRequest!!
    assertEquals("android", sent.header("source"))
    assertEquals("application/vnd.api+json; charset=utf-8", sent.header("Accept"))
    assertEquals("application/json", sent.header("Content-Type"))
  }

  @Test
  fun intercept_withoutAuthData_omitsAuthHeaders() = testScope.runTest {
    val dataSource = NatPreferencesDataSource(
      InMemoryDataStore(UserPreferences.getDefaultInstance()),
    )
    val interceptor = AuthInterceptor(dataSource)
    val chain = RecordingChain(Request.Builder().url("https://example.com/").build())

    interceptor.intercept(chain)

    val sent = chain.proceededRequest!!
    assertNull(sent.header("access-token"))
    assertNull(sent.header("token-type"))
    assertNull(sent.header("client"))
    assertNull(sent.header("expiry"))
    assertNull(sent.header("uid"))
    assertEquals("android", sent.header("source"))
  }

  @Test
  fun intercept_preservesOriginalRequestUrl() = testScope.runTest {
    val dataSource = NatPreferencesDataSource(
      InMemoryDataStore(UserPreferences.getDefaultInstance()),
    )
    val interceptor = AuthInterceptor(dataSource)
    val originalUrl = "https://example.com/path?query=value"
    val chain = RecordingChain(Request.Builder().url(originalUrl).build())

    interceptor.intercept(chain)

    assertEquals(originalUrl, chain.proceededRequest!!.url.toString())
  }
}

private class RecordingChain(private val request: Request) : Interceptor.Chain {
  var proceededRequest: Request? = null

  override fun request(): Request = request

  override fun proceed(request: Request): Response {
    proceededRequest = request
    return Response.Builder()
      .request(request)
      .protocol(Protocol.HTTP_1_1)
      .code(200)
      .message("OK")
      .body("".toResponseBody(null))
      .build()
  }

  override fun connection(): Connection? = null
  override fun call(): Call = error("not used")
  override fun connectTimeoutMillis(): Int = 0
  override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = error("not used")
  override fun readTimeoutMillis(): Int = 0
  override fun withReadTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = error("not used")
  override fun writeTimeoutMillis(): Int = 0
  override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = error("not used")
}
