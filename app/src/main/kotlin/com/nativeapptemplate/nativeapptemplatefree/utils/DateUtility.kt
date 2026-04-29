package com.nativeapptemplate.nativeapptemplatefree.utils

import java.time.ZoneId
import java.time.ZonedDateTime

object DateUtility {
  fun ZonedDateTime.cardDateString(): String {
    val dateTimeFormatter = DateTimeFormatterUtility.cardDateFormatter()
    return this.format(dateTimeFormatter)
  }

  fun String.cardDateString(zoneId: ZoneId = ZoneId.systemDefault()): String {
    if (this.isBlank()) return ""

    val date = ZonedDateTime.parse(this).withZoneSameInstant(zoneId)
    return date.cardDateString()
  }

  fun ZonedDateTime.cardTimeString(): String {
    val dateTimeFormatter = DateTimeFormatterUtility.cardTimeFormatter()
    return this.format(dateTimeFormatter)
  }

  fun String.cardTimeString(zoneId: ZoneId = ZoneId.systemDefault()): String {
    if (this.isBlank()) return ""

    val date = ZonedDateTime.parse(this).withZoneSameInstant(zoneId)
    return date.cardTimeString()
  }

  fun ZonedDateTime.cardDateTimeString(): String {
    return "${this.cardDateString()} ${this.cardTimeString()}"
  }

  fun String.cardDateTimeString(zoneId: ZoneId = ZoneId.systemDefault()): String {
    if (this.isBlank()) return ""

    val date = ZonedDateTime.parse(this).withZoneSameInstant(zoneId)
    return date.cardDateTimeString()
  }
}
