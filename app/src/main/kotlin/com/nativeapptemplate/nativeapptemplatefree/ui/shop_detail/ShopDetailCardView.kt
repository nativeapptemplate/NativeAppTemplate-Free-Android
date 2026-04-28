package com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
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
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(16.dp),
  ) {
    val nameFontSize = with(LocalDensity.current) { MaterialTheme.typography.titleLarge.fontSize.value.dp.toSp() }
    val timestampFontSize = with(LocalDensity.current) { MaterialTheme.typography.bodySmall.fontSize.value.dp.toSp() }
    val completedAt = data.getCompletedAt()

    Text(
      data.getName(),
      style = MaterialTheme.typography.titleLarge,
      fontSize = nameFontSize,
      modifier = Modifier
        .weight(1f)
        .padding(end = 8.dp),
    )

    // TODO: removed in Phase 2A-2 — scanState/customerReadAt column dropped with ItemTag schema v2

    Column(
      horizontalAlignment = Alignment.End,
    ) {
      data.getItemTagState()?.let { itemTagState ->
        when (itemTagState) {
          ItemTagState.Completed -> {
            CompletedTag()

            Text(
              completedAt.cardDateTimeString(),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier
                .padding(top = 4.dp),
              fontSize = timestampFontSize,
            )
          }
          else -> {
            IdlingTag()
          }
        }
      }
    }
  }
}
