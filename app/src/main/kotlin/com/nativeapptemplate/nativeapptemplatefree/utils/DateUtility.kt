package com.nativeapptemplate.nativeapptemplatefree.utils

import android.text.format.DateUtils
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

  fun ZonedDateTime.cardTimeAgoInWordsDateString(): String {
    return DateUtils.getRelativeTimeSpanString(this.toInstant().toEpochMilli(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString()
  }

  fun String.cardTimeAgoInWordsDateString(zoneId: ZoneId = ZoneId.systemDefault()): String {
    if (this.isBlank()) return ""

    val date = ZonedDateTime.parse(this).withZoneSameInstant(zoneId)
    return date.cardTimeAgoInWordsDateString()
  }
}