package com.nativeapptemplate.nativeapptemplatefree.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.contentColorFor
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The text color Material picks for a surface (`Surface`, `Card`, `Dialog` and the sheets call
 * `contentColorFor` on their container). `contentColorFor` matches `errorContainer` (and the other
 * containers) before `surface`, so a theme that gives `errorContainer` the surface's color turns the
 * text of every plain surface into `onErrorContainer`, the error red (found in Orange Baton, whose
 * palette did exactly that). Text on the surfaces and the background is onSurface / onBackground.
 */
class ContentColorTest {
  @Test
  fun surfaces_getOnSurface() {
    assertSurfacesGet(LightDefaultColorScheme)
    assertSurfacesGet(DarkDefaultColorScheme)
  }

  private fun assertSurfacesGet(scheme: ColorScheme) {
    val surfaces: Map<String, Pair<Color, Color>> = mapOf(
      "surface" to (scheme.surface to scheme.onSurface),
      "surfaceContainerLow" to (scheme.surfaceContainerLow to scheme.onSurface),
      "surfaceContainer" to (scheme.surfaceContainer to scheme.onSurface),
      "surfaceContainerHigh" to (scheme.surfaceContainerHigh to scheme.onSurface),
      "surfaceContainerHighest" to (scheme.surfaceContainerHighest to scheme.onSurface),
      "background" to (scheme.background to scheme.onBackground),
    )
    for ((name, colors) in surfaces) {
      val (container, text) = colors
      assertEquals(name, text, scheme.contentColorFor(container))
    }
  }
}
