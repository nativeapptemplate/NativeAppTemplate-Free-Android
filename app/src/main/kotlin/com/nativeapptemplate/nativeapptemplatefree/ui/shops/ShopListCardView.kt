package com.nativeapptemplate.nativeapptemplatefree.ui.shops

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
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

        Column(
          modifier = Modifier
            .padding(top = 16.dp),
        ) {
          CountRow(icon = Icons.Outlined.People, count = data.getScannedItemTagsCount(), countLabel = "tags scanned by customers")
          CountRow(icon = Icons.Outlined.Flag, count = data.getCompletedItemTagsCount(), countLabel = "completed tags")
          CountRow(icon = Icons.Outlined.Rectangle, count = data.getItemTagsCount(), countLabel = "all tags")
        }

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

@Composable
private fun CountRow(
  icon: ImageVector,
  count: Int,
  countLabel: String,
) {
  Column {
    Row(verticalAlignment = Alignment.Bottom) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      Text(
        text = count.toString(),
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Right,
        modifier = Modifier
          .width(32.dp)
      )

      Text(
        text = countLabel,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
          .padding(start = 8.dp)
      )
    }
  }
}
