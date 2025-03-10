package com.nativeapptemplate.nativeapptemplatefree.ui.common.tags

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.NatTheme
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NonScaledSp.nonScaledSp

@Composable
fun TagView(
  text: String,
  textColor: Color,
  backgroundColor: Color,
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .background(backgroundColor),
  ) {
    Text(
      modifier = Modifier
        .align(Alignment.Center)
        .padding(horizontal = 8.dp),
      text = text,
      color = textColor,
      lineHeight = 20.sp.nonScaledSp,
      fontSize = 12.sp.nonScaledSp,
      fontWeight = FontWeight.Bold,
    )
  }
}

@Preview
@Composable
private fun TagViewPreview() {
  NatTheme {
    TagView(
      text = "COMPLETE",
      textColor = MaterialTheme.colorScheme.onError,
      backgroundColor = MaterialTheme.colorScheme.error,
    )
  }
}
