package com.nativeapptemplate.nativeapptemplatefree.ui.common.tags

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.NatTheme

@Composable
fun IdlingTag() {
  TagView(
    text = "IDLING",
    textColor = MaterialTheme.colorScheme.onSurfaceVariant,
    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
  )
}

@Preview
@Composable
private fun IdlingTagPreview() {
  NatTheme {
    IdlingTag()
  }
}
