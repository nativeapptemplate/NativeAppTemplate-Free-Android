package com.nativeapptemplate.nativeapptemplatefree.utils

import com.nativeapptemplate.nativeapptemplatefree.utils.DateUtility.cardDateString
import com.nativeapptemplate.nativeapptemplatefree.utils.DateUtility.cardDateTimeString
import com.nativeapptemplate.nativeapptemplatefree.utils.DateUtility.cardTimeString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class DateUtilityTest {

  private val testZonedDateTime = ZonedDateTime.of(
    2025,
    3,
    15,
    14,
    30,
    0,
    0,
    ZoneId.of("UTC"),
  )

  // ZonedDateTime extension tests

  @Test
  fun zonedDateTime_cardDateString_formatsCorrectly() {
    val result = testZonedDateTime.cardDateString()
    // Locale-safe: verify it contains day and year
    assertTrue(result.contains("15"))
    assertTrue(result.contains("2025"))
  }

  @Test
  fun zonedDateTime_cardTimeString_formatsCorrectly() {
    assertEquals("14:30", testZonedDateTime.cardTimeString())
  }

  @Test
  fun zonedDateTime_cardDateTimeString_combinesDateAndTime() {
    val result = testZonedDateTime.cardDateTimeString()
    assertTrue(result.contains("15"))
    assertTrue(result.contains("2025"))
    assertTrue(result.contains("14:30"))
  }

  @Test
  fun string_cardDateTimeString_returnsEmptyForBlankString() {
    assertEquals("", "".cardDateTimeString())
  }

  // String extension tests with UTC zone

  @Test
  fun string_cardDateString_formatsIsoStringWithUtcZone() {
    val isoString = "2025-03-15T14:30:00Z"
    val result = isoString.cardDateString(ZoneId.of("UTC"))
    assertTrue(result.contains("15"))
    assertTrue(result.contains("2025"))
  }

  @Test
  fun string_cardTimeString_formatsIsoStringWithUtcZone() {
    val isoString = "2025-03-15T14:30:00Z"
    assertEquals("14:30", isoString.cardTimeString(ZoneId.of("UTC")))
  }

  // Blank string tests

  @Test
  fun string_cardDateString_returnsEmptyForBlankString() {
    assertEquals("", "".cardDateString())
  }

  @Test
  fun string_cardTimeString_returnsEmptyForBlankString() {
    assertEquals("", "".cardTimeString())
  }

  @Test
  fun string_cardDateString_returnsEmptyForWhitespaceString() {
    assertEquals("", "   ".cardDateString())
  }

  @Test
  fun string_cardTimeString_returnsEmptyForWhitespaceString() {
    assertEquals("", "   ".cardTimeString())
  }

  // Timezone conversion tests

  @Test
  fun string_cardDateString_convertsTimezoneCorrectly() {
    // 2025-03-15T23:30:00Z in UTC is 2025-03-16 08:30 in Asia/Tokyo (+9)
    val isoString = "2025-03-15T23:30:00Z"
    val result = isoString.cardDateString(ZoneId.of("Asia/Tokyo"))
    assertTrue(result.contains("16"))
    assertTrue(result.contains("2025"))
  }

  @Test
  fun string_cardTimeString_convertsTimezoneCorrectly() {
    // 2025-03-15T14:30:00Z in UTC is 23:30 in Asia/Tokyo (+9)
    val isoString = "2025-03-15T14:30:00Z"
    assertEquals("23:30", isoString.cardTimeString(ZoneId.of("Asia/Tokyo")))
  }
}
