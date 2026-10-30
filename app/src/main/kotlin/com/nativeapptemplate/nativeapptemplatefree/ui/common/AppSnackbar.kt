package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * How tall the app's snackbar is now (0 when none is shown). The app's Scaffold shows it over the
 * screens, whose own Scaffolds do not know of it, so it would cover their floating buttons.
 */
val LocalAppSnackbarHeight = compositionLocalOf { 0.dp }

/** The app's snackbar host, which reports its height for [LocalAppSnackbarHeight]. */
@Composable
fun AppSnackbarHost(
  hostState: SnackbarHostState,
  onHeightChange: (Dp) -> Unit,
) {
  val density = LocalDensity.current
  SnackbarHost(
    hostState = hostState,
    modifier = Modifier.onSizeChanged { onHeightChange(with(density) { it.height.toDp() }) },
  )
}

/** Lifts a screen's floating button above the app's snackbar while one is shown. */
fun Modifier.aboveAppSnackbar(): Modifier = composed {
  val height by animateDpAsState(LocalAppSnackbarHeight.current, label = LIFT_ANIMATION_LABEL)
  padding(bottom = height)
}

/** Names the animation for the Compose tooling, not shown to anyone. */
private const val LIFT_ANIMATION_LABEL = "aboveAppSnackbar"
