package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MainButtonView(
  title: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.onSurface,
  titleColor: Color = MaterialTheme.colorScheme.onSurface,
  enabled: Boolean = true,
) {
  OutlinedButton(
    onClick = {
      onClick()
    },
    border = BorderStroke(2.dp, color),
    shape = RoundedCornerShape(20),
    colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
    enabled = enabled,
    modifier = modifier
      .padding(horizontal = 12.dp)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(
        title,
        style = MaterialTheme.typography.titleLarge,
        color = titleColor,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
      )
    }
  }
}
