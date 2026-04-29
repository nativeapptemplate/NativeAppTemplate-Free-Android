package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import androidx.lifecycle.ViewModel
import com.nativeapptemplate.nativeapptemplatefree.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {
  companion object {
    val onboardings: List<Onboarding> = listOf(
      Onboarding(id = 1, imageOrientation = ImageOrientation.LANDSCAPE),
      Onboarding(id = 2, imageOrientation = ImageOrientation.LANDSCAPE),
      Onboarding(id = 3, imageOrientation = ImageOrientation.PORTRAIT),
      Onboarding(id = 4, imageOrientation = ImageOrientation.PORTRAIT),
    )

    fun onboardingDescription(id: Int): Int = when (id) {
      1 -> R.string.onboarding_description1
      2 -> R.string.onboarding_description2
      3 -> R.string.onboarding_description3
      4 -> R.string.onboarding_description4
      else -> R.string.onboarding_description1
    }

    fun onboardingImageId(id: Int): Int = when (id) {
      1 -> R.drawable.ic_overview1
      2 -> R.drawable.ic_overview2
      3 -> R.drawable.ic_overview3
      4 -> R.drawable.ic_overview4
      else -> R.drawable.ic_overview1
    }
  }
}
