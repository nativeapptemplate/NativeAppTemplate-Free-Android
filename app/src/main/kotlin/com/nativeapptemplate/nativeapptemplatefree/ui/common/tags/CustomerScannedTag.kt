package com.nativeapptemplate.nativeapptemplatefree.ui.common.tags

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.NatTheme

@Composable
fun CustomerScannedTag() {
  TagView(
    text = "CUSTOMER SCANNED",
    textColor = MaterialTheme.colorScheme.onError,
    backgroundColor = MaterialTheme.colorScheme.error,
  )
}

@Preview
@Composable
private fun CustomerScannedTagPreview() {
  NatTheme {
    CustomerScannedTag()
  }
}
