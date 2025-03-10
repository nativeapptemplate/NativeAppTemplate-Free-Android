package com.nativeapptemplate.nativeapptemplatefree.utils

import java.time.format.DateTimeFormatter

object DateTimeFormatterUtility {
  private const val CARD_DATE_STRING: String = "MMM dd yyyy"
  private const val CARD_YEAR_STRING: String = "yyyy"
  private const val CARD_MONTH_STRING: String = "MMM"
  private const val CARD_DAY_STRING: String = "dd"
  private const val CARD_WEEK_DAY_STRING: String = "EEE"
  private const val CARD_TIME_STRING: String = "HH:mm"

  fun cardDateFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern(CARD_DATE_STRING)
  }

  fun cardYearFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern(CARD_YEAR_STRING)
  }

  fun cardMonthFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern(CARD_MONTH_STRING)
  }

  fun cardDayFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern(CARD_DAY_STRING)
  }

  fun cardWeekDayFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern(CARD_WEEK_DAY_STRING)
  }

  fun cardTimeFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern(CARD_TIME_STRING)
  }
}