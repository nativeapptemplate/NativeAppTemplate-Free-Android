package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.NatTheme

@Composable
fun LoadingView() {
  Box(
    contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator(
      modifier = Modifier
        .align(Alignment.Center)
        .size(80.dp),
      color = MaterialTheme.colorScheme.onBackground,
    )
  }
}
@Preview
@Composable
private fun LoadingStatePreview() {
  NatTheme {
    LoadingView()
  }
}
