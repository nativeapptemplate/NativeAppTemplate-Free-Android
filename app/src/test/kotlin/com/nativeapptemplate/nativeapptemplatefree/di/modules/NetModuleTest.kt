package com.nativeapptemplate.nativeapptemplatefree.di.modules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NetModuleTest {

  @Test
  fun certificatePinnerFor_pinsEachConfiguredHashToTheApiDomain() {
    val pinner = certificatePinnerFor(
      domain = "api.example.test",
      pins = " sha256/54Il7gpV4QvX8fAyEKV+6fp8VGjgHqIAAqF5bLCfYNQ= ,sha256/kIdp6NNEd8wsugYyyIYFsi1ylMCED3hZbSR8ZFsa/A4=",
    )!!

    assertEquals(setOf("api.example.test"), pinner.pins.map { it.pattern }.toSet())
    assertEquals(2, pinner.pins.size)
  }

  @Test
  fun certificatePinnerFor_noConfiguredPins_meansNoPinning() {
    // A self-hosted API must not be pinned to someone else's certificate.
    assertNull(certificatePinnerFor(domain = "api.example.test", pins = ""))
  }
}
