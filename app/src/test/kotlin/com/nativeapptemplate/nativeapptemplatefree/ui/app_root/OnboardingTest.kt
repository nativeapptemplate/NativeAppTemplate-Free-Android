package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class OnboardingTest {
  @Test
  fun onboarding_defaultsToLandscape() {
    val onboarding = Onboarding(id = 1)
    assertEquals(ImageOrientation.LANDSCAPE, onboarding.imageOrientation)
  }

  @Test
  fun onboarding_acceptsExplicitOrientation() {
    val onboarding = Onboarding(id = 2, imageOrientation = ImageOrientation.PORTRAIT)
    assertEquals(ImageOrientation.PORTRAIT, onboarding.imageOrientation)
  }

  @Test
  fun onboarding_equalsWhenIdAndOrientationMatch() {
    val a = Onboarding(id = 3, imageOrientation = ImageOrientation.PORTRAIT)
    val b = Onboarding(id = 3, imageOrientation = ImageOrientation.PORTRAIT)
    assertEquals(a, b)
  }

  @Test
  fun onboarding_differsWhenOrientationDiffers() {
    val a = Onboarding(id = 4, imageOrientation = ImageOrientation.PORTRAIT)
    val b = Onboarding(id = 4, imageOrientation = ImageOrientation.LANDSCAPE)
    assertNotEquals(a, b)
  }

  @Test
  fun imageOrientation_hasPortraitAndLandscape() {
    val values = ImageOrientation.entries.map { it.name }
    assertEquals(setOf("PORTRAIT", "LANDSCAPE"), values.toSet())
  }
}
