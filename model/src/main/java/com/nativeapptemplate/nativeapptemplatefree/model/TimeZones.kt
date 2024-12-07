package com.nativeapptemplate.nativeapptemplatefree.model

import android.icu.util.TimeZone

/**
 * TimeZone utilities
 */
object TimeZones {
  /**
   * default time zone value
   */
  const val DEFAULT_TIME_ZONE: String = "London"

  /**
   * Time zones from Ruby on Rails
   */
  val map: LinkedHashMap<String, String> = linkedMapOf(
    "International Date Line West" to "(GMT-12:00) International Date Line West",
    "American Samoa" to "(GMT-11:00) American Samoa",
    "Midway Island" to "(GMT-11:00) Midway Island",
    "Hawaii" to "(GMT-10:00) Hawaii",
    "Alaska" to "(GMT-09:00) Alaska",
    "Pacific Time (US & Canada)" to "(GMT-08:00) Pacific Time (US & Canada)",
    "Tijuana" to "(GMT-08:00) Tijuana",
    "Arizona" to "(GMT-07:00) Arizona",
    "Mazatlan" to "(GMT-07:00) Mazatlan",
    "Mountain Time (US & Canada)" to "(GMT-07:00) Mountain Time (US & Canada)",
    "Central America" to "(GMT-06:00) Central America",
    "Central Time (US & Canada)" to "(GMT-06:00) Central Time (US & Canada)",
    "Chihuahua" to "(GMT-06:00) Chihuahua",
    "Guadalajara" to "(GMT-06:00) Guadalajara",
    "Mexico City" to "(GMT-06:00) Mexico City",
    "Monterrey" to "(GMT-06:00) Monterrey",
    "Saskatchewan" to "(GMT-06:00) Saskatchewan",
    "Bogota" to "(GMT-05:00) Bogota",
    "Eastern Time (US & Canada)" to "(GMT-05:00) Eastern Time (US & Canada)",
    "Indiana (East)" to "(GMT-05:00) Indiana (East)",
    "Lima" to "(GMT-05:00) Lima",
    "Quito" to "(GMT-05:00) Quito",
    "Atlantic Time (Canada)" to "(GMT-04:00) Atlantic Time (Canada)",
    "Caracas" to "(GMT-04:00) Caracas",
    "Georgetown" to "(GMT-04:00) Georgetown",
    "La Paz" to "(GMT-04:00) La Paz",
    "Puerto Rico" to "(GMT-04:00) Puerto Rico",
    "Santiago" to "(GMT-04:00) Santiago",
    "Newfoundland" to "(GMT-03:30) Newfoundland",
    "Brasilia" to "(GMT-03:00) Brasilia",
    "Buenos Aires" to "(GMT-03:00) Buenos Aires",
    "Greenland" to "(GMT-03:00) Greenland",
    "Montevideo" to "(GMT-03:00) Montevideo",
    "Mid-Atlantic" to "(GMT-02:00) Mid-Atlantic",
    "Azores" to "(GMT-01:00) Azores",
    "Cape Verde Is." to "(GMT-01:00) Cape Verde Is.",
    "Edinburgh" to "(GMT+00:00) Edinburgh",
    "Lisbon" to "(GMT+00:00) Lisbon",
    "London" to "(GMT+00:00) London",
    "Monrovia" to "(GMT+00:00) Monrovia",
    "UTC" to "(GMT+00:00) UTC",
    "Amsterdam" to "(GMT+01:00) Amsterdam",
    "Belgrade" to "(GMT+01:00) Belgrade",
    "Berlin" to "(GMT+01:00) Berlin",
    "Bern" to "(GMT+01:00) Bern",
    "Bratislava" to "(GMT+01:00) Bratislava",
    "Brussels" to "(GMT+01:00) Brussels",
    "Budapest" to "(GMT+01:00) Budapest",
    "Casablanca" to "(GMT+01:00) Casablanca",
    "Copenhagen" to "(GMT+01:00) Copenhagen",
    "Dublin" to "(GMT+01:00) Dublin",
    "Ljubljana" to "(GMT+01:00) Ljubljana",
    "Madrid" to "(GMT+01:00) Madrid",
    "Paris" to "(GMT+01:00) Paris",
    "Prague" to "(GMT+01:00) Prague",
    "Rome" to "(GMT+01:00) Rome",
    "Sarajevo" to "(GMT+01:00) Sarajevo",
    "Skopje" to "(GMT+01:00) Skopje",
    "Stockholm" to "(GMT+01:00) Stockholm",
    "Vienna" to "(GMT+01:00) Vienna",
    "Warsaw" to "(GMT+01:00) Warsaw",
    "West Central Africa" to "(GMT+01:00) West Central Africa",
    "Zagreb" to "(GMT+01:00) Zagreb",
    "Zurich" to "(GMT+01:00) Zurich",
    "Athens" to "(GMT+02:00) Athens",
    "Bucharest" to "(GMT+02:00) Bucharest",
    "Cairo" to "(GMT+02:00) Cairo",
    "Harare" to "(GMT+02:00) Harare",
    "Helsinki" to "(GMT+02:00) Helsinki",
    "Jerusalem" to "(GMT+02:00) Jerusalem",
    "Kaliningrad" to "(GMT+02:00) Kaliningrad",
    "Kyiv" to "(GMT+02:00) Kyiv",
    "Pretoria" to "(GMT+02:00) Pretoria",
    "Riga" to "(GMT+02:00) Riga",
    "Sofia" to "(GMT+02:00) Sofia",
    "Tallinn" to "(GMT+02:00) Tallinn",
    "Vilnius" to "(GMT+02:00) Vilnius",
    "Baghdad" to "(GMT+03:00) Baghdad",
    "Istanbul" to "(GMT+03:00) Istanbul",
    "Kuwait" to "(GMT+03:00) Kuwait",
    "Minsk" to "(GMT+03:00) Minsk",
    "Moscow" to "(GMT+03:00) Moscow",
    "Nairobi" to "(GMT+03:00) Nairobi",
    "Riyadh" to "(GMT+03:00) Riyadh",
    "St. Petersburg" to "(GMT+03:00) St. Petersburg",
    "Volgograd" to "(GMT+03:00) Volgograd",
    "Tehran" to "(GMT+03:30) Tehran",
    "Abu Dhabi" to "(GMT+04:00) Abu Dhabi",
    "Baku" to "(GMT+04:00) Baku",
    "Muscat" to "(GMT+04:00) Muscat",
    "Samara" to "(GMT+04:00) Samara",
    "Tbilisi" to "(GMT+04:00) Tbilisi",
    "Yerevan" to "(GMT+04:00) Yerevan",
    "Kabul" to "(GMT+04:30) Kabul",
    "Ekaterinburg" to "(GMT+05:00) Ekaterinburg",
    "Islamabad" to "(GMT+05:00) Islamabad",
    "Karachi" to "(GMT+05:00) Karachi",
    "Tashkent" to "(GMT+05:00) Tashkent",
    "Chennai" to "(GMT+05:30) Chennai",
    "Kolkata" to "(GMT+05:30) Kolkata",
    "Mumbai" to "(GMT+05:30) Mumbai",
    "New Delhi" to "(GMT+05:30) New Delhi",
    "Sri Jayawardenepura" to "(GMT+05:30) Sri Jayawardenepura",
    "Kathmandu" to "(GMT+05:45) Kathmandu",
    "Almaty" to "(GMT+06:00) Almaty",
    "Astana" to "(GMT+06:00) Astana",
    "Dhaka" to "(GMT+06:00) Dhaka",
    "Urumqi" to "(GMT+06:00) Urumqi",
    "Rangoon" to "(GMT+06:30) Rangoon",
    "Bangkok" to "(GMT+07:00) Bangkok",
    "Hanoi" to "(GMT+07:00) Hanoi",
    "Jakarta" to "(GMT+07:00) Jakarta",
    "Krasnoyarsk" to "(GMT+07:00) Krasnoyarsk",
    "Novosibirsk" to "(GMT+07:00) Novosibirsk",
    "Beijing" to "(GMT+08:00) Beijing",
    "Chongqing" to "(GMT+08:00) Chongqing",
    "Hong Kong" to "(GMT+08:00) Hong Kong",
    "Irkutsk" to "(GMT+08:00) Irkutsk",
    "Kuala Lumpur" to "(GMT+08:00) Kuala Lumpur",
    "Perth" to "(GMT+08:00) Perth",
    "Singapore" to "(GMT+08:00) Singapore",
    "Taipei" to "(GMT+08:00) Taipei",
    "Ulaanbaatar" to "(GMT+08:00) Ulaanbaatar",
    "Osaka" to "(GMT+09:00) Osaka",
    "Sapporo" to "(GMT+09:00) Sapporo",
    "Seoul" to "(GMT+09:00) Seoul",
    "Tokyo" to "(GMT+09:00) Tokyo",
    "Yakutsk" to "(GMT+09:00) Yakutsk",
    "Adelaide" to "(GMT+09:30) Adelaide",
    "Darwin" to "(GMT+09:30) Darwin",
    "Brisbane" to "(GMT+10:00) Brisbane",
    "Canberra" to "(GMT+10:00) Canberra",
    "Guam" to "(GMT+10:00) Guam",
    "Hobart" to "(GMT+10:00) Hobart",
    "Melbourne" to "(GMT+10:00) Melbourne",
    "Port Moresby" to "(GMT+10:00) Port Moresby",
    "Sydney" to "(GMT+10:00) Sydney",
    "Vladivostok" to "(GMT+10:00) Vladivostok",
    "Magadan" to "(GMT+11:00) Magadan",
    "New Caledonia" to "(GMT+11:00) New Caledonia",
    "Solomon Is." to "(GMT+11:00) Solomon Is.",
    "Srednekolymsk" to "(GMT+11:00) Srednekolymsk",
    "Auckland" to "(GMT+12:00) Auckland",
    "Fiji" to "(GMT+12:00) Fiji",
    "Kamchatka" to "(GMT+12:00) Kamchatka",
    "Marshall Is." to "(GMT+12:00) Marshall Is.",
    "Wellington" to "(GMT+12:00) Wellington",
    "Chatham Is." to "(GMT+12:45) Chatham Is.",
    "Nuku'alofa" to "(GMT+13:00) Nuku'alofa",
    "Samoa" to "(GMT+13:00) Samoa",
    "Tokelau Is." to "(GMT+13:00) Tokelau Is."
  )

  /**
   * Get default time zone
   */
  fun currentTimeZoneKey(): String {
    var result = DEFAULT_TIME_ZONE
    val timeZone = TimeZone.getDefault()

    val timeZoneGmt = timeZone.getDisplayName(false, TimeZone.LONG_GMT)
    val timeZoneLocation = timeZone.id

    val timeZoneLocationDetail = timeZoneLocation.split("/").last().replace("_", " ")

    val filteredTimeZoneValues = map.values.filter { timeZoneGmt in it }

    if (filteredTimeZoneValues.isEmpty()) return result

    val resultTimeZoneValue = map.values.firstOrNull { timeZoneLocationDetail in it }
    resultTimeZoneValue?.let {
      return keyFromValue(it)
    }

    result = keyFromValue(filteredTimeZoneValues.first())

    return result
  }

  fun keyFromValue(timeZoneValue: String): String {
    return map.filterValues { it == timeZoneValue }.keys.first()
  }
}
