package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import androidx.lifecycle.ViewModel
import com.nativeapptemplate.nativeapptemplatefree.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
) : ViewModel() {
  companion object {
    fun onboardingDescription(index: Int): Int {
      val description = when (index) {
        0 -> R.string.onboarding_description1
        1 -> R.string.onboarding_description2
        2 -> R.string.onboarding_description3
        else -> R.string.onboarding_description1
      }

      return description
    }

    fun onboardingImageId(index: Int): Int {
      val imageId = when (index) {
        0 -> R.drawable.ic_overview1
        1 -> R.drawable.ic_overview2
        2 -> R.drawable.ic_overview3
        else -> R.drawable.ic_overview1
      }

      return imageId
    }
  }
}
