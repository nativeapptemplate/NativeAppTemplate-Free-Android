package com.nativeapptemplate.nativeapptemplatefree.utils

import com.nativeapptemplate.nativeapptemplatefree.utils.DateUtility.cardDateTimeString
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId

class DateUtilityTest {

  @Test
  fun string_cardDateTimeString_withUtcZone_formatsCorrectly() {
    val dateString = "2025-06-15T14:30:00Z"
    assertEquals("2025/06/15 14:30", dateString.cardDateTimeString(ZoneId.of("UTC")))
  }

  @Test
  fun string_cardDateTimeString_blankReturnsEmpty() {
    assertEquals("", "".cardDateTimeString())
  }

  @Test
  fun string_cardDateTimeString_convertsTimezone() {
    // UTC 14:30 -> Tokyo (UTC+9) is 23:30 same day
    val dateString = "2025-06-15T14:30:00Z"
    assertEquals("2025/06/15 23:30", dateString.cardDateTimeString(ZoneId.of("Asia/Tokyo")))
  }
}
