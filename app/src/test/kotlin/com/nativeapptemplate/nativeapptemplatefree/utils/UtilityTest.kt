package com.nativeapptemplate.nativeapptemplatefree.utils

import com.nativeapptemplate.nativeapptemplatefree.utils.Utility.validateEmail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UtilityTest {

  // validateEmail tests

  @Test
  fun validateEmail_validEmail_returnsTrue() {
    assertTrue("test@example.com".validateEmail())
  }

  @Test
  fun validateEmail_emptyString_returnsFalse() {
    assertFalse("".validateEmail())
  }

  @Test
  fun validateEmail_noAtSign_returnsFalse() {
    assertFalse("testexample.com".validateEmail())
  }

  @Test
  fun validateEmail_noDomain_returnsFalse() {
    assertFalse("test@".validateEmail())
  }

  // isAlphanumeric tests

  @Test
  fun isAlphanumeric_alphanumericText_returnsTrue() {
    assertTrue(Utility.isAlphanumeric("abc123"))
  }

  @Test
  fun isAlphanumeric_lettersOnly_returnsTrue() {
    assertTrue(Utility.isAlphanumeric("abcdef"))
  }

  @Test
  fun isAlphanumeric_numbersOnly_returnsTrue() {
    assertTrue(Utility.isAlphanumeric("123456"))
  }

  @Test
  fun isAlphanumeric_specialChars_returnsFalse() {
    assertFalse(Utility.isAlphanumeric("abc!@#"))
  }

  @Test
  fun isAlphanumeric_null_returnsFalse() {
    assertFalse(Utility.isAlphanumeric(null))
  }

  @Test
  fun isAlphanumeric_blank_returnsFalse() {
    assertFalse(Utility.isAlphanumeric(""))
  }

  // scanUri tests

  @Test
  fun scanUri_serverType_usesScanPath() {
    val uri = Utility.scanUri("test-id", "server")
    assertTrue(uri.toString().contains("/scan?"))
  }

  @Test
  fun scanUri_customerType_usesScanCustomerPath() {
    val uri = Utility.scanUri("test-id", "customer")
    assertTrue(uri.toString().contains("/scan_customer?"))
  }

  @Test
  fun scanUri_containsItemTagId() {
    val uri = Utility.scanUri("test-id-123", "server")
    assertEquals("test-id-123", uri.getQueryParameter("item_tag_id"))
  }

  @Test
  fun scanUri_containsType() {
    val uri = Utility.scanUri("test-id", "server")
    assertEquals("server", uri.getQueryParameter("type"))
  }
}
