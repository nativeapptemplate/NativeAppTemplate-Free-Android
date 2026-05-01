package com.nativeapptemplate.nativeapptemplatefree.utils

import com.nativeapptemplate.nativeapptemplatefree.utils.Utility.isValidEmail
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UtilityTest {

  // isValidEmail tests

  @Test
  fun isValidEmail_validEmail_returnsTrue() {
    assertTrue("test@example.com".isValidEmail())
  }

  @Test
  fun isValidEmail_emptyString_returnsFalse() {
    assertFalse("".isValidEmail())
  }

  @Test
  fun isValidEmail_noAtSign_returnsFalse() {
    assertFalse("testexample.com".isValidEmail())
  }

  @Test
  fun isValidEmail_noDomain_returnsFalse() {
    assertFalse("test@".isValidEmail())
  }
}
