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

  // Accra is GMT+00:00 and not in the map. The first "(GMT+00:00)" entry in the map is Edinburgh;
  // it used to pick "International Date Line West" (GMT-12:00), since "GMT" is in every entry.
  @Test
  fun currentTimeZoneKey_atGmtInACityNotListed_isTheFirstZoneAtGmt() {
    assertEquals("Edinburgh", TimeZones.currentTimeZoneKey(TimeZone.getTimeZone("Africa/Accra")))
  }

  // The zone "GMT" (common on emulators) has the city part "GMT", which every entry contains, so
  // matching it against the whole map picked "International Date Line West" (GMT-12:00). No
  // "(GMT+00:00)" entry is named "GMT", so it is the first one at that offset: Edinburgh.
  @Test
  fun currentTimeZoneKey_forTheZoneNamedGmt_isTheFirstZoneAtGmt() {
    assertEquals("Edinburgh", TimeZones.currentTimeZoneKey(TimeZone.getTimeZone("GMT")))
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
}
