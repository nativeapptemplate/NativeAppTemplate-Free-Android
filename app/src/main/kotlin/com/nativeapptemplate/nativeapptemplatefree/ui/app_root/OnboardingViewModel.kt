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
        3 -> R.string.onboarding_description4
        4 -> R.string.onboarding_description5
        5 -> R.string.onboarding_description6
        6 -> R.string.onboarding_description7
        7 -> R.string.onboarding_description8
        8 -> R.string.onboarding_description9
        9 -> R.string.onboarding_description10
        10 -> R.string.onboarding_description11
        11 -> R.string.onboarding_description12
        12 -> R.string.onboarding_description13
        else -> R.string.onboarding_description1
      }

      return description
    }

    fun onboardingImageId(index: Int): Int {
      val imageId = when (index) {
        0 -> R.drawable.ic_overview1
        1 -> R.drawable.ic_overview2
        2 -> R.drawable.ic_overview3
        3 -> R.drawable.ic_overview4
        4 -> R.drawable.ic_overview5
        5 -> R.drawable.ic_overview6
        6 -> R.drawable.ic_overview7
        7 -> R.drawable.ic_overview8
        8 -> R.drawable.ic_overview9
        9 -> R.drawable.ic_overview10
        10 -> R.drawable.ic_overview11
        11 -> R.drawable.ic_overview12
        12 -> R.drawable.ic_overview13
        else -> R.drawable.ic_overview1
      }

      return imageId
    }
  }
}
