package com.nativeapptemplate.nativeapptemplatefree.utils

import java.time.format.DateTimeFormatter

object DateTimeFormatterUtility {
  private const val CARD_DATE_STRING: String = "MMM dd yyyy"
  private const val CARD_TIME_STRING: String = "HH:mm"

  fun cardDateFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern(CARD_DATE_STRING)
  }

  fun cardTimeFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern(CARD_TIME_STRING)
  }
}