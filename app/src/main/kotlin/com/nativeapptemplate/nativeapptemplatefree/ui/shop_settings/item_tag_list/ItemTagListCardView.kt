package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagState
import com.nativeapptemplate.nativeapptemplatefree.ui.common.tags.CompletedTag
import com.nativeapptemplate.nativeapptemplatefree.ui.common.tags.IdlingTag
import com.nativeapptemplate.nativeapptemplatefree.utils.DateUtility.cardDateTimeString

@Composable
fun ItemTagListCardView(
  data: Data,
  onItemClick: (String) -> Unit,
) {
  val description = data.getDescription()
  val state = data.getItemTagState()
  val completedAt = data.getCompletedAt()

  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onItemClick(data.id!!) }
      .padding(horizontal = 16.dp, vertical = 12.dp),
  ) {
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      Text(
        data.getName(),
        style = MaterialTheme.typography.titleMedium,
      )
      if (description.isNotBlank()) {
        Text(
          description,
          style = MaterialTheme.typography.bodySmall,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
    Column(
      horizontalAlignment = Alignment.End,
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      when (state) {
        ItemTagState.Completed -> {
          CompletedTag()
          if (completedAt.isNotBlank()) {
            Text(
              completedAt.cardDateTimeString(),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
        ItemTagState.Idled -> IdlingTag()
        null -> Unit
      }
    }
  }
}
