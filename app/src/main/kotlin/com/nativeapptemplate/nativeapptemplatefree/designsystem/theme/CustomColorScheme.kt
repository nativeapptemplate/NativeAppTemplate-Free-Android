package com.nativeapptemplate.nativeapptemplatefree.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColorScheme(
  val success: Color = Color.Unspecified,
  val onSuccess: Color = Color.Unspecified,
  val successContainer: Color = Color.Unspecified,
  val onSuccessContainer: Color = Color.Unspecified,
)

val LightCustomColorScheme = CustomColorScheme(
  success = Teal40,
  onSuccess = Color.White,
  successContainer = Teal90,
  onSuccessContainer = Teal10,
)

val DarkCustomColorScheme = CustomColorScheme(
  success = Teal80,
  onSuccess = Teal20,
  successContainer = Teal30,
  onSuccessContainer = Teal90,
)

val LocalCustomColorScheme = staticCompositionLocalOf { CustomColorScheme() }