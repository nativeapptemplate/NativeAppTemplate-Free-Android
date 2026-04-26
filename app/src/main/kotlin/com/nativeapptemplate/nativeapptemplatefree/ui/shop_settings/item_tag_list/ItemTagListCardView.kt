package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
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

  ListItem(
    headlineContent = {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          data.getName(),
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.weight(1f),
        )
        when (state) {
          ItemTagState.Completed -> CompletedTag()
          ItemTagState.Idled -> IdlingTag()
          null -> Unit
        }
      }
    },
    supportingContent = if (description.isBlank() && state != ItemTagState.Completed) {
      null
    } else {
      {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          if (description.isNotBlank()) {
            Text(
              description,
              style = MaterialTheme.typography.bodySmall,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          if (state == ItemTagState.Completed && completedAt.isNotBlank()) {
            Text(
              completedAt.cardDateTimeString(),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      }
    },
    modifier = Modifier
      .clickable {
        onItemClick(data.id!!)
      },
  )
}
