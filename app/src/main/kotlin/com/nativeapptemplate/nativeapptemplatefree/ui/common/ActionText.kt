package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NonScaledSp.nonScaledSp

//https://github.com/philipplackner/ComposeSwipeToReveal
@Composable
fun ActionText(
  onClick: () -> Unit,
  backgroundColor: Color,
  text: String,
  modifier: Modifier = Modifier,
) {
  IconButton(
    onClick = onClick,
    modifier = modifier
      .background(backgroundColor)
  ) {
    Text(
      text = text,
      modifier = Modifier.requiredWidth(64.dp),
      fontSize = 12.sp.nonScaledSp,
      maxLines = 1,
      textAlign = TextAlign.Center,
      color = Color.White,
      fontWeight = FontWeight.Bold,
    )
  }
}