package com.nativeapptemplate.nativeapptemplatefree.ui.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.FlagCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagState
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagType
import com.nativeapptemplate.nativeapptemplatefree.model.ScanState
import com.nativeapptemplate.nativeapptemplatefree.model.ShowTagInfoScanResult
import com.nativeapptemplate.nativeapptemplatefree.model.ShowTagInfoScanResultType
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NonScaledSp.nonScaledSp
import com.nativeapptemplate.nativeapptemplatefree.ui.common.tags.CompletedTag
import com.nativeapptemplate.nativeapptemplatefree.ui.common.tags.IdlingTag
import com.nativeapptemplate.nativeapptemplatefree.utils.DateUtility.cardDateString
import com.nativeapptemplate.nativeapptemplatefree.utils.DateUtility.cardTimeAgoInWordsDateString
import com.nativeapptemplate.nativeapptemplatefree.utils.DateUtility.cardTimeString

@Composable
fun ShowTagInfoScanResultView(
  showTagInfoScanResult: ShowTagInfoScanResult,
) {
  ContentView(
    showTagInfoScanResult = showTagInfoScanResult,
  )
}

@Composable
private fun ContentView(
  showTagInfoScanResult: ShowTagInfoScanResult,
) {
  when (showTagInfoScanResult.showTagInfoScanResultType) {
    ShowTagInfoScanResultType.Succeeded -> SucceededView(showTagInfoScanResult)
    ShowTagInfoScanResultType.Failed -> FailedView(showTagInfoScanResult)
    ShowTagInfoScanResultType.Idled -> IdledView()
  }
}

@Composable
private fun SucceededView(
  showTagInfoScanResult: ShowTagInfoScanResult,
) {
  val itemTagType = showTagInfoScanResult.itemTagInfoFromNdefMessage.itemTagType
  val itemTagData = showTagInfoScanResult.itemTagData
  val itemTagTypeColor = if (itemTagType == ItemTagType.Server) Color.Red else Color.Blue
  val isReadOnly = showTagInfoScanResult.itemTagInfoFromNdefMessage.isReadOnly
  val displayReadOnly = if (isReadOnly) stringResource(R.string.read_only) else stringResource(R.string.writable)

  val fontSizeMedium = 16
  val fontSizeLarge = 20
  val lineHeightMedium = 20
  val lineHeightLarge = 24

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseSurface),
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 24.dp)
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier
        .padding(24.dp)
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          Icons.Outlined.Rectangle,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.inverseOnSurface,
        )

        Text(
          stringResource(R.string.tag_info),
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.inverseOnSurface,
        )
      }

      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth(),
      ) {
        Text(
          showTagInfoScanResult.itemTagData.queueNumber,
          color = itemTagTypeColor,
          style = MaterialTheme.typography.displaySmall
        )
      }

      Row(
        horizontalArrangement = Arrangement
          .spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally,
          ),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
          .fillMaxWidth(),
      ) {
        Text(
          showTagInfoScanResult.itemTagInfoFromNdefMessage.scannedAt.cardTimeAgoInWordsDateString(),
          fontSize = fontSizeLarge.sp.nonScaledSp,
          lineHeight = lineHeightLarge.sp.nonScaledSp,
          color = MaterialTheme.colorScheme.inverseOnSurface,
          style = MaterialTheme.typography.titleMedium,
        )
        Text(
          "show tag info scanned",
          fontSize = fontSizeMedium.sp.nonScaledSp,
          lineHeight = lineHeightMedium.sp.nonScaledSp,
          color = MaterialTheme.colorScheme.inverseOnSurface,
        )
      }

      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
          .padding(top = 16.dp),
      ) {
        Row(
          horizontalArrangement = Arrangement
            .spacedBy(
              space = 8.dp,
              alignment = Alignment.CenterHorizontally,
            ),
          verticalAlignment = Alignment.Bottom
        ) {
          Icon(
            Icons.Outlined.Storefront,
            contentDescription = null
          )

          Text(
            itemTagData.shopName,
            fontSize = fontSizeLarge.sp.nonScaledSp,
            lineHeight = lineHeightLarge.sp.nonScaledSp,
            color = MaterialTheme.colorScheme.inverseOnSurface,
            style = MaterialTheme.typography.titleLarge,
          )
        }

        InfoRow(
          Icons.Outlined.Info,
          "tag type"
        ) {
          Text(
            itemTagType.title,
            fontSize = fontSizeLarge.sp.nonScaledSp,
            lineHeight = lineHeightLarge.sp.nonScaledSp,
            color = itemTagTypeColor,
            style = MaterialTheme.typography.titleLarge
          )
        }

        InfoRow(
          Icons.Outlined.Flag,
          "tag status"
        ) {
          when (itemTagData.state) {
            ItemTagState.Completed -> CompletedTag()
            else -> IdlingTag()
          }
        }

        if (itemTagData.scanState == ScanState.Scanned && itemTagData.customerReadAt.isNotBlank()) {
          InfoRow(
            Icons.Outlined.People,
            "scanned by a customer"
          ) {
            Text(
              itemTagData.customerReadAt.cardTimeString(),
              fontSize = fontSizeLarge.sp.nonScaledSp,
              lineHeight = lineHeightLarge.sp.nonScaledSp,
              color = MaterialTheme.colorScheme.inverseOnSurface,
              style = MaterialTheme.typography.titleLarge
            )
          }
        }

        if (itemTagData.state == ItemTagState.Completed && itemTagData.completedAt.isNotBlank()) {
          InfoRow(
            Icons.Outlined.FlagCircle,
            "completed"
          ) {
            Text(
              itemTagData.completedAt.cardTimeString(),
              fontSize = fontSizeLarge.sp.nonScaledSp,
              lineHeight = lineHeightLarge.sp.nonScaledSp,
              color = MaterialTheme.colorScheme.inverseOnSurface,
              style = MaterialTheme.typography.titleLarge
            )
          }
        }

        InfoRow(
          Icons.Outlined.Rectangle,
          "NFC tag"
        ) {
          Text(
            displayReadOnly,
            fontSize = fontSizeLarge.sp.nonScaledSp,
            lineHeight = lineHeightLarge.sp.nonScaledSp,
            color = MaterialTheme.colorScheme.inverseOnSurface,
            style = MaterialTheme.typography.titleLarge
          )
        }

        InfoRow(
          Icons.Outlined.AccessTime,
          "created"
        ) {
          Text(
            itemTagData.createdAt.cardDateString(),
            fontSize = fontSizeLarge.sp.nonScaledSp,
            lineHeight = lineHeightLarge.sp.nonScaledSp,
            color = MaterialTheme.colorScheme.inverseOnSurface,
            style = MaterialTheme.typography.titleLarge
          )
        }
      }
    }
  }
}

@Composable
private fun FailedView(
  showTagInfoScanResult: ShowTagInfoScanResult,
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 24.dp)
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier
        .padding(24.dp)
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          Icons.Outlined.Error,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onErrorContainer,
        )

        Text(
          "Error",
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onErrorContainer,
        )
      }

      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth(),
      ) {
        Text(
          showTagInfoScanResult.message,
          color = MaterialTheme.colorScheme.onErrorContainer,
        )
      }
    }
  }
}

@Composable
private fun IdledView(
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseSurface),
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 24.dp)
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier
        .padding(24.dp)
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          Icons.Outlined.Rectangle,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.inverseOnSurface,
        )

        Text(
          "Tag Info",
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.inverseOnSurface,
        )
      }
    }
  }
}

@Composable
private fun InfoRow(
  icon: ImageVector,
  infoLabel: String,
  content: @Composable () -> Unit,
) {
  val fontSizeMedium = 16
  val lineHeightMedium = 20

  Column(
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Row(
      horizontalArrangement = Arrangement
        .spacedBy(
          space = 8.dp,
          alignment = Alignment.CenterHorizontally,
        ),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null
      )

      Row(
        modifier = Modifier
          .width(128.dp)
      ) {
        content()
      }

      Text(
        text = infoLabel,
        fontSize = fontSizeMedium.sp.nonScaledSp,
        lineHeight = lineHeightMedium.sp.nonScaledSp,
        modifier = Modifier
          .padding(start = 8.dp)
      )
    }
  }
}
