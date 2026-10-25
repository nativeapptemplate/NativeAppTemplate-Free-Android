package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.DarkDefaultColorScheme
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.LightDefaultColorScheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The kinds of MainButtonView, as iOS's MainButtonType: before them every button was the same
 * onSurface outline, so signing out or deleting looked like signing in (found in Orange Baton).
 * Expected values: WCAG 2 contrast, 4.5:1 for the button's text, which is also the outline.
 */
class MainButtonTypeTest {
  @Test
  fun eachKindLooksDifferent() {
    for (scheme in listOf(LightDefaultColorScheme, DarkDefaultColorScheme)) {
      val primary = MainButtonType.PRIMARY.colors(scheme)
      val secondary = MainButtonType.SECONDARY.colors(scheme)
      val destructive = MainButtonType.DESTRUCTIVE.colors(scheme)

      // Only the main action is filled.
      assertEquals(scheme.primary, primary.container)
      assertEquals(Color.Transparent, secondary.container)
      assertEquals(Color.Transparent, destructive.container)
      // A destructive action is in the error color, never in the main action's.
      assertEquals(scheme.error, destructive.content)
      assertNotEquals(secondary.content, destructive.content)
    }
  }

  @Test
  fun textIsReadable() {
    assertReadable(LightDefaultColorScheme)
    assertReadable(DarkDefaultColorScheme)
  }

  private fun assertReadable(scheme: ColorScheme) {
    for (type in MainButtonType.entries) {
      val colors = type.colors(scheme)
      val behind = colors.container.compositeOver(scheme.background)
      val ratio = contrast(colors.content, behind)
      assertTrue("$type: $ratio", ratio >= 4.5)
    }
  }

  private fun contrast(a: Color, b: Color): Double {
    val (light, dark) = listOf(a.luminance(), b.luminance()).sortedDescending()
    return (light + 0.05) / (dark + 0.05)
  }
}
