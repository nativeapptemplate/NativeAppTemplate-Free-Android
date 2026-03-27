package com.nativeapptemplate.nativeapptemplatefree.utils

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class DateTimeFormatterUtilityTest {

  private val testDateTime = LocalDateTime.of(2025, 3, 15, 14, 30, 0)

  @Test
  fun cardDateFormatter_isNotNull() {
    assertNotNull(DateTimeFormatterUtility.cardDateFormatter())
  }

  @Test
  fun cardDateFormatter_formatsDateWithMonthDayYear() {
    val formatted = testDateTime.format(DateTimeFormatterUtility.cardDateFormatter())
    // Locale-safe: verify it contains day and year regardless of month name locale
    assertTrue(formatted.contains("15"))
    assertTrue(formatted.contains("2025"))
  }

  @Test
  fun cardTimeFormatter_isNotNull() {
    assertNotNull(DateTimeFormatterUtility.cardTimeFormatter())
  }

  @Test
  fun cardTimeFormatter_formatsTimeAsHoursAndMinutes() {
    val formatted = testDateTime.format(DateTimeFormatterUtility.cardTimeFormatter())
    assertTrue(formatted.contains("14"))
    assertTrue(formatted.contains("30"))
  }
}
