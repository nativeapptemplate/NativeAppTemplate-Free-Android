package com.nativeapptemplate.nativeapptemplatefree.ui.shops

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nativeapptemplate.nativeapptemplatefree.model.Data

@Composable
fun ShopListCardView(
  data: Data,
  onClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
      .clickable { onClick() },
  ) {
    Box(
      modifier = Modifier.padding(16.dp),
    ) {
      Column {
        Text(
          data.getName(),
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
          modifier = Modifier.fillMaxWidth(),
        )

        Text(
          data.getDescription(),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier
            .padding(top = 12.dp)
        )
      }
    }
  }
}
