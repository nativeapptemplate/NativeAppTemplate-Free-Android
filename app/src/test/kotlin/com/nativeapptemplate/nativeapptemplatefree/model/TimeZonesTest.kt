package com.nativeapptemplate.nativeapptemplatefree.model

import android.icu.util.TimeZone
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Locale

// Robolectric for android.icu. The rule (same as the iOS app's Utility.currentTimeZone): the zone
// named after the identifier's city, else the first one in TimeZones.map with the zone's standard offset.
@RunWith(RobolectricTestRunner::class)
class TimeZonesTest {
  private val originalLocale = Locale.getDefault()

  @After
  fun tearDown() {
    Locale.setDefault(originalLocale)
  }

  @Test
  fun currentTimeZoneKey_namedAfterTheCity_isThatZone() {
    // "(GMT+09:00) Tokyo" is in TimeZones.map.
    assertEquals("Tokyo", TimeZones.currentTimeZoneKey(TimeZone.getTimeZone("Asia/Tokyo")))
  }

  // Accra is GMT+00:00 and not in the map. The first "(GMT+00:00)" entry in Rails order is
  // Casablanca (see the oracle below); it used to pick "International Date Line West"
  // (GMT-12:00), since "GMT" is in every entry.
  @Test
  fun currentTimeZoneKey_atGmtInACityNotListed_isTheFirstZoneAtGmt() {
    assertEquals("Casablanca", TimeZones.currentTimeZoneKey(TimeZone.getTimeZone("Africa/Accra")))
  }

  // The zone "GMT" (common on emulators) has the city part "GMT", which every entry contains, so
  // matching it against the whole map picked "International Date Line West" (GMT-12:00). No
  // "(GMT+00:00)" entry is named "GMT", so it is the first one at that offset in Rails order (see
  // the oracle below): Casablanca.
  @Test
  fun currentTimeZoneKey_forTheZoneNamedGmt_isTheFirstZoneAtGmt() {
    assertEquals("Casablanca", TimeZones.currentTimeZoneKey(TimeZone.getTimeZone("GMT")))
  }

  // Australia/West is GMT+08:00. Its city part "West" is in "International Date Line West"
  // (GMT-12:00), but no "(GMT+08:00)" entry is named "West", so it is the first one at +08:00:
  // Beijing.
  @Test
  fun currentTimeZoneKey_whenTheCityNamesAZoneAtAnotherOffset_isTheFirstZoneAtItsOffset() {
    assertEquals("Beijing", TimeZones.currentTimeZoneKey(TimeZone.getTimeZone("Australia/West")))
  }

  // A French device names the offset "UTC+01:00", which no entry contains; Paris is in the map.
  @Test
  fun currentTimeZoneKey_onAFrenchDevice_isStillTheCity() {
    Locale.setDefault(Locale.FRANCE)

    assertEquals("Paris", TimeZones.currentTimeZoneKey(TimeZone.getTimeZone("Europe/Paris")))
  }

  // Paraguay is "(GMT-03:00) Asuncion" in Rails (see the oracle below); the app had no entry for
  // it. Paraguay has been on permanent GMT-03:00 since tzdata 2024b. The zone is built by hand
  // because Robolectric's ICU data (sdk 35) still has Asuncion at GMT-04:00, and the city is only
  // matched among the zones at its standard offset.
  @Test
  fun currentTimeZoneKey_asuncion_isAsuncion() {
    val asuncion = TimeZone.getTimeZone("America/Asuncion").apply { rawOffset = -3 * 60 * 60 * 1000 }

    assertEquals("Asuncion", TimeZones.currentTimeZoneKey(asuncion))
  }

  // Rails' "Greenland" is America/Godthab, i.e. America/Nuuk, whose standard offset is GMT-02:00
  // since tzdata 2023a (Rails labels it "(GMT-02:00) Greenland"). "Nuuk" names no entry, so the
  // first "(GMT-02:00)" entry is picked: Greenland in Rails order (Montevideo, Greenland,
  // Mid-Atlantic). The zone is built by hand because Robolectric's ICU data (sdk 35) still has
  // Nuuk at GMT-03:00; a device with current tzdata reports GMT-02:00.
  @Test
  fun currentTimeZoneKey_nuuk_isGreenland() {
    val nuuk = TimeZone.getTimeZone("America/Nuuk").apply { rawOffset = -2 * 60 * 60 * 1000 }

    assertEquals("Greenland", TimeZones.currentTimeZoneKey(nuuk))
  }

  // The API validates time_zone against ActiveSupport::TimeZone.all, so the app must offer exactly
  // those zones, with the same labels, in the same order.
  @Test
  fun map_isTheRailsTimeZoneList() {
    assertEquals(railsTimeZones.toList(), TimeZones.map.toList())
  }

  private companion object {
    // Oracle: the API's (nativeapptemplateapi, activesupport 8.1.4) output of
    //   bin/rails runner 'ActiveSupport::TimeZone.all.each { |z| puts "#{z.name}\t#{z.to_s}" }'
    // in Rails order (name to label).
    val railsTimeZones: List<Pair<String, String>> = listOf(
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
      "Asuncion" to "(GMT-03:00) Asuncion",
      "Brasilia" to "(GMT-03:00) Brasilia",
      "Buenos Aires" to "(GMT-03:00) Buenos Aires",
      "Montevideo" to "(GMT-03:00) Montevideo",
      "Greenland" to "(GMT-02:00) Greenland",
      "Mid-Atlantic" to "(GMT-02:00) Mid-Atlantic",
      "Azores" to "(GMT-01:00) Azores",
      "Cape Verde Is." to "(GMT-01:00) Cape Verde Is.",
      "Casablanca" to "(GMT+00:00) Casablanca",
      "Dublin" to "(GMT+00:00) Dublin",
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
      "Copenhagen" to "(GMT+01:00) Copenhagen",
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
      "Almaty" to "(GMT+05:00) Almaty",
      "Astana" to "(GMT+05:00) Astana",
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
      "Tokelau Is." to "(GMT+13:00) Tokelau Is.",
    )
  }
}
