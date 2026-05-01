package com.nativeapptemplate.nativeapptemplatefree.utils

import java.time.ZoneId
import java.time.ZonedDateTime

object DateUtility {
  fun String.cardDateTimeString(zoneId: ZoneId = ZoneId.systemDefault()): String {
    if (this.isBlank()) return ""

    val date = ZonedDateTime.parse(this).withZoneSameInstant(zoneId)
    val dateString = date.format(DateTimeFormatterUtility.cardDateFormatter())
    val timeString = date.format(DateTimeFormatterUtility.cardTimeFormatter())
    return "$dateString $timeString"
  }
}
