package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * The round check-mark button that submits a form. [label] is required: the button shows only an
 * icon, so it is what TalkBack announces.
 */
@Composable
fun SubmitFab(
  label: String,
  onClick: () -> Unit,
  enabled: Boolean = true,
) {
  // FloatingActionButton doesn't support the enabled property
  // https://stackoverflow.com/a/68853697/1160200
  Button(
    onClick = onClick,
    modifier = Modifier.defaultMinSize(minWidth = 64.dp, minHeight = 64.dp),
    enabled = enabled,
    shape = CircleShape,
  ) {
    Icon(Icons.Filled.Done, contentDescription = label)
  }
}
