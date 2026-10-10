package com.nativeapptemplate.nativeapptemplatefree.ui.common.tags

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.LocalCustomColorScheme
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.NativeAppTemplateTheme

@Composable
fun CompletedTag() {
  TagView(
    text = stringResource(R.string.tag_completed),
    textColor = LocalCustomColorScheme.current.onSuccess,
    backgroundColor = LocalCustomColorScheme.current.success,
  )
}

@Preview
@Composable
private fun CompletedTagPreview() {
  NativeAppTemplateTheme {
    CompletedTag()
  }
}
