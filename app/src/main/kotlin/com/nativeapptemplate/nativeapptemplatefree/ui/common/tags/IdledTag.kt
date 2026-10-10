package com.nativeapptemplate.nativeapptemplatefree.ui.common.tags

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.NativeAppTemplateTheme

@Composable
fun IdledTag() {
  TagView(
    text = stringResource(R.string.tag_idled),
    textColor = MaterialTheme.colorScheme.onSurfaceVariant,
    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
  )
}

@Preview
@Composable
private fun IdledTagPreview() {
  NativeAppTemplateTheme {
    IdledTag()
  }
}
