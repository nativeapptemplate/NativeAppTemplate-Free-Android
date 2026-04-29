package com.nativeapptemplate.nativeapptemplatefree.utils

import com.nativeapptemplate.nativeapptemplatefree.utils.Utility.validateEmail
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
}
