package com.nativeapptemplate.nativeapptemplatefree.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RequestHelperTest {

  @Test
  fun getHeaders_withoutAuth_containsBaseHeaders() {
    val helper = RequestHelper()
    val headers = helper.getHeaders()

    assertEquals("android", headers["source"])
    assertEquals("application/vnd.api+json; charset=utf-8", headers["Accept"])
    assertEquals("application/json", headers["Content-Type"])
  }

  @Test
  fun getHeaders_withoutAuth_doesNotContainAuthHeaders() {
    val helper = RequestHelper()
    val headers = helper.getHeaders()

    assertFalse(headers.containsKey("access-token"))
    assertFalse(headers.containsKey("token-type"))
    assertFalse(headers.containsKey("client"))
    assertFalse(headers.containsKey("expiry"))
    assertFalse(headers.containsKey("uid"))
  }

  @Test
  fun getHeaders_withAuth_containsAuthHeaders() {
    val helper = RequestHelper(
      apiAuthToken = "test-token",
      client = "test-client",
      expiry = "12345",
      uid = "test@example.com",
    )
    val headers = helper.getHeaders()

    assertEquals("test-token", headers["access-token"])
    assertEquals("Bearer ", headers["token-type"])
    assertEquals("test-client", headers["client"])
    assertEquals("12345", headers["expiry"])
    assertEquals("test@example.com", headers["uid"])
  }

  @Test
  fun getHeaders_withAuth_stillContainsBaseHeaders() {
    val helper = RequestHelper(
      apiAuthToken = "test-token",
      client = "test-client",
      expiry = "12345",
      uid = "test@example.com",
    )
    val headers = helper.getHeaders()

    assertEquals("android", headers["source"])
    assertEquals("application/vnd.api+json; charset=utf-8", headers["Accept"])
    assertEquals("application/json", headers["Content-Type"])
  }

  @Test
  fun getHeaders_withEmptyToken_doesNotContainAuthHeaders() {
    val helper = RequestHelper(
      apiAuthToken = "",
      client = "test-client",
      expiry = "12345",
      uid = "test@example.com",
    )
    val headers = helper.getHeaders()

    assertFalse(headers.containsKey("access-token"))
    assertFalse(headers.containsKey("token-type"))
  }

  @Test
  fun getHeaders_containsClientNameAndVersion() {
    val helper = RequestHelper()
    val headers = helper.getHeaders()

    assertNotNull(headers["client-name"])
    assertNotNull(headers["client-version"])
    assertTrue(headers["client-name"]!!.isNotEmpty())
    assertTrue(headers["client-version"]!!.isNotEmpty())
  }
}
