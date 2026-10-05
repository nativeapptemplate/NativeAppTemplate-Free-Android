package com.nativeapptemplate.nativeapptemplatefree.utils

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException

object DateUtility {
  fun String.cardDateTimeString(zoneId: ZoneId = ZoneId.systemDefault()): String {
    if (this.isBlank()) return ""

    // Called during composition, so an unexpected format must not crash the screen.
    val date = try {
      ZonedDateTime.parse(this).withZoneSameInstant(zoneId)
    } catch (_: DateTimeParseException) {
      return this
    }
    val dateString = date.format(DateTimeFormatterUtility.cardDateFormatter())
    val timeString = date.format(DateTimeFormatterUtility.cardTimeFormatter())
    return "$dateString $timeString"
  }
}
