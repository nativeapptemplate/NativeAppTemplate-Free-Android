package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nativeapptemplate.nativeapptemplatefree.model.Data

@Composable
fun ItemTagListCardView(
  data: Data,
  onItemClick: (String) -> Unit,
) {
  ListItem(
    headlineContent = {
      Text(
        data.getQueueNumber(),
        style = MaterialTheme.typography.titleMedium
      )
    },
    modifier = Modifier
      .clickable {
        onItemClick(data.id!!)
      },
  )
}
