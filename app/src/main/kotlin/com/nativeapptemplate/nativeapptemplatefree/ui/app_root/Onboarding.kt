package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

enum class ImageOrientation {
  PORTRAIT,
  LANDSCAPE,
}

data class Onboarding(
  val id: Int,
  val imageOrientation: ImageOrientation = ImageOrientation.LANDSCAPE,
)
