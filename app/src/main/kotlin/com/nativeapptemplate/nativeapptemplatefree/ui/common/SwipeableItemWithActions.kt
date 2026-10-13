package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// https://github.com/philipplackner/ComposeSwipeToReveal
/** A row that reveals [actions] when swiped left. The row itself remembers whether it is open. */
@Composable
fun SwipeableItemWithActions(
  actions: @Composable RowScope.() -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  var contextMenuWidth by remember {
    mutableFloatStateOf(0f)
  }
  val offset = remember {
    Animatable(initialValue = 0f)
  }
  val scope = rememberCoroutineScope()

  // A new actions width (e.g. on the first layout) starts the row closed.
  LaunchedEffect(contextMenuWidth) {
    offset.animateTo(0f)
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(IntrinsicSize.Min),
  ) {
    Row(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .onSizeChanged {
          contextMenuWidth = it.width.toFloat()
        },
      verticalAlignment = Alignment.CenterVertically,
    ) {
      actions()
    }
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .offset { IntOffset(offset.value.roundToInt(), 0) }
        .pointerInput(contextMenuWidth) {
          detectHorizontalDragGestures(
            onHorizontalDrag = { _, dragAmount ->
              scope.launch {
                val newOffset = (offset.value + dragAmount)
                  .coerceIn(-contextMenuWidth, 0f)
                offset.snapTo(newOffset)
              }
            },
            onDragEnd = {
              when {
                offset.value <= -contextMenuWidth / 2f -> {
                  scope.launch {
                    offset.animateTo(-contextMenuWidth)
                  }
                }

                else -> {
                  scope.launch {
                    offset.animateTo(0f)
                  }
                }
              }
            },
          )
        },
    ) {
      content()
    }
  }
}
