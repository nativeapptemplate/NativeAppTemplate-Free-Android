package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * The kinds of iOS's MainButtonType (MainButtonView.swift): [PRIMARY] is filled with `primary`
 * under `onPrimary`, for the screen's main action; [SECONDARY] is a `primary` outline; [DESTRUCTIVE]
 * an `error` outline (sign out, delete, decline), never filled. Each screen uses the kind of the
 * iOS screen's button, so a destructive action does not look like the main one.
 */
enum class MainButtonType { PRIMARY, SECONDARY, DESTRUCTIVE }

/** [container] is transparent for the outlined kinds, whose [content] color is also the outline. */
internal data class MainButtonColors(val container: Color, val content: Color)

internal fun MainButtonType.colors(scheme: ColorScheme): MainButtonColors = when (this) {
  MainButtonType.PRIMARY -> MainButtonColors(container = scheme.primary, content = scheme.onPrimary)
  MainButtonType.SECONDARY -> MainButtonColors(container = Color.Transparent, content = scheme.primary)
  MainButtonType.DESTRUCTIVE -> MainButtonColors(container = Color.Transparent, content = scheme.error)
}

@Composable
fun MainButtonView(
  title: String,
  type: MainButtonType,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  val colors = type.colors(MaterialTheme.colorScheme)
  val shape = RoundedCornerShape(20)
  val buttonModifier = modifier.padding(horizontal = 12.dp)
  val label: @Composable () -> Unit = {
    Text(
      title,
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center,
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
    )
  }
  when (type) {
    MainButtonType.PRIMARY -> Button(
      onClick = onClick,
      shape = shape,
      colors = ButtonDefaults.buttonColors(containerColor = colors.container, contentColor = colors.content),
      enabled = enabled,
      modifier = buttonModifier,
    ) { label() }

    MainButtonType.SECONDARY, MainButtonType.DESTRUCTIVE -> OutlinedButton(
      onClick = onClick,
      shape = shape,
      border = BorderStroke(2.dp, if (enabled) colors.content else colors.content.copy(alpha = 0.38f)),
      colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.content),
      enabled = enabled,
      modifier = buttonModifier,
    ) { label() }
  }
}
