package com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.nativeapptemplate.nativeapptemplatefree.model.ScanState
import com.nativeapptemplate.nativeapptemplatefree.ui.common.tags.CompletedTag
import com.nativeapptemplate.nativeapptemplatefree.ui.common.tags.CustomerScannedTag
import com.nativeapptemplate.nativeapptemplatefree.ui.common.tags.IdlingTag
import com.nativeapptemplate.nativeapptemplatefree.utils.DateUtility.cardTimeString

@Composable
fun ShopDetailCardView(
  data: Data,
) {
  Row(
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(16.dp),
  ) {
    val queueNumberFontSize = with(LocalDensity.current) { MaterialTheme.typography.titleLarge.fontSize.value.dp.toSp() }
    val timestampFontSize = with(LocalDensity.current) { MaterialTheme.typography.bodySmall.fontSize.value.dp.toSp() }
    val customerReadAt = data.getCustomerReadAt()
    val completedAt = data.getCompletedAt()

    Text(
      data.getQueueNumber(),
      style = MaterialTheme.typography.titleLarge,
      fontSize = queueNumberFontSize,
    )

    Spacer(modifier = Modifier.weight(1f))

    Column(
      horizontalAlignment = Alignment.End
    ) {

      data.getScanState()?.let { scanState ->
        if (scanState == ScanState.Scanned) {
          CustomerScannedTag()
          Text(
            customerReadAt.cardTimeString(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
              .padding(top = 4.dp),
            fontSize = timestampFontSize,
          )
        }
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    Column(
      horizontalAlignment = Alignment.End
    ) {
      data.getItemTagState()?.let { itemTagState ->
        when (itemTagState) {
          ItemTagState.Completed -> {
            CompletedTag()

            Text(
              completedAt.cardTimeString(),
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
