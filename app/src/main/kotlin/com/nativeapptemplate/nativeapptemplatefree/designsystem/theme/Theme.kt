package com.nativeapptemplate.nativeapptemplatefree.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// When you give several roles one color (your palette's surface also as errorContainer, say), give
// their on* colors one color too. A Surface, Card or Dialog without colors takes its text from
// contentColorFor, which matches errorContainer before surface: with onErrorContainer left red, the
// text of every plain surface turns red (ContentColorTest). FABs state their colors for the same
// reason: the default primaryContainer, made the screen's color, would hide them.
val LightDefaultColorScheme = lightColorScheme(
  primary = Blue40,
  onPrimary = Color.White,
  primaryContainer = Blue90,
  onPrimaryContainer = Blue10,
  secondary = DarkBlue40,
  onSecondary = Color.White,
  secondaryContainer = DarkBlue90,
  onSecondaryContainer = DarkBlue10,
  tertiary = Yellow40,
  onTertiary = Color.White,
  tertiaryContainer = Yellow90,
  onTertiaryContainer = Yellow10,
  error = Red40,
  onError = Color.White,
  errorContainer = Red90,
  onErrorContainer = Red10,
  background = DarkBlueGray99,
  onBackground = DarkBlueGray10,
  surface = DarkBlueGray99,
  onSurface = DarkBlueGray10,
  surfaceVariant = BlueGray90,
  onSurfaceVariant = BlueGray40,
  inverseSurface = DarkBlueGray20,
  inverseOnSurface = DarkBlueGray95,
  outline = BlueGray50,
)

val DarkDefaultColorScheme = darkColorScheme(
  primary = Blue90,
  onPrimary = Blue20,
  primaryContainer = Blue30,
  onPrimaryContainer = Blue90,
  secondary = DarkBlue80,
  onSecondary = DarkBlue20,
  secondaryContainer = DarkBlue30,
  onSecondaryContainer = DarkBlue90,
  tertiary = Yellow90,
  onTertiary = Yellow20,
  tertiaryContainer = Yellow30,
  onTertiaryContainer = Yellow90,
  error = Red90,
  onError = Red20,
  errorContainer = Red30,
  onErrorContainer = Red90,
  background = DarkBlueGray10,
  onBackground = DarkBlueGray90,
  surface = DarkBlueGray10,
  onSurface = DarkBlueGray90,
  surfaceVariant = BlueGray30,
  onSurfaceVariant = BlueGray70,
  inverseSurface = DarkBlueGray90,
  inverseOnSurface = DarkBlueGray10,
  outline = BlueGray60,
)

@Composable
fun NativeAppTemplateTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkDefaultColorScheme else LightDefaultColorScheme

  val backgroundTheme = BackgroundTheme(
    color = colorScheme.surface,
    tonalElevation = 2.dp,
  )

  val customColorsPalette =
    if (darkTheme) {
      DarkCustomColorScheme
    } else {
      LightCustomColorScheme
    }

  // Composition locals
  CompositionLocalProvider(
    LocalCustomColorScheme provides customColorsPalette,
    LocalBackgroundTheme provides backgroundTheme,
  ) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = NativeAppTemplateTypography,
      content = content,
    )
  }
}
