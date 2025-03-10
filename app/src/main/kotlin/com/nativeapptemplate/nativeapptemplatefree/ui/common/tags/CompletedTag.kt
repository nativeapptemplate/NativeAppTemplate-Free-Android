package com.nativeapptemplate.nativeapptemplatefree.ui.common.tags

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.LocalCustomColorScheme
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.NatTheme

@Composable
fun CompletedTag() {
  TagView(
    text = "COMPLETED",
    textColor = LocalCustomColorScheme.current.onSuccess,
    backgroundColor = LocalCustomColorScheme.current.success,
  )
}

@Preview
@Composable
private fun CompletedTagPreview() {
  NatTheme {
    CompletedTag()
  }
}
