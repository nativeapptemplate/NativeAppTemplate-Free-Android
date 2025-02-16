package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import com.nativeapptemplate.nativeapptemplatefree.R
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class OnboardingViewModelTest {
  @Test
  fun onboardingDescription_isValid() = runTest {
    assertEquals(OnboardingViewModel.onboardingDescription(0), R.string.onboarding_description1)
  }

  @Test
  fun onboardingImageId_isValid() = runTest {
    assertEquals(OnboardingViewModel.onboardingImageId(0), R.drawable.ic_overview1)
  }
}