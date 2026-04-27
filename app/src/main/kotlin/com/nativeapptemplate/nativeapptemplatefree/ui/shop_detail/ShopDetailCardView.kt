package com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
fun ShopDetailCardView(
  data: Data,
) {
  val description = data.getDescription()
  val state = data.getItemTagState()
  val completedAt = data.getCompletedAt()

  Row(
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(16.dp),
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(2.dp),
      modifier = Modifier.weight(1f),
    ) {
      Text(
        data.getName(),
        style = MaterialTheme.typography.titleLarge,
      )
      if (description.isNotBlank()) {
        Text(
          description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }

    Spacer(modifier = Modifier.padding(horizontal = 8.dp))

    Column(
      horizontalAlignment = Alignment.End,
      verticalArrangement = Arrangement.spacedBy(4.dp),
      modifier = Modifier.widthIn(min = 82.dp),
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
