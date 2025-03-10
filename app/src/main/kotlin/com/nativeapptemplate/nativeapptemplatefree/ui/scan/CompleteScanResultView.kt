package com.nativeapptemplate.nativeapptemplatefree.ui.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.LocalCustomColorScheme
import com.nativeapptemplate.nativeapptemplatefree.model.CompleteScanResult
import com.nativeapptemplate.nativeapptemplatefree.model.CompleteScanResultType
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NonScaledSp.nonScaledSp
import com.nativeapptemplate.nativeapptemplatefree.ui.common.tags.CompletedTag
import com.nativeapptemplate.nativeapptemplatefree.ui.common.tags.IdlingTag
import com.nativeapptemplate.nativeapptemplatefree.utils.DateUtility.cardTimeAgoInWordsDateString

@Composable
fun CompleteScanResultView(
  completeScanResult: CompleteScanResult,
) {
  ContentView(
    completeScanResult = completeScanResult,
  )
}

@Composable
private fun ContentView(
  completeScanResult: CompleteScanResult,
) {
  when (completeScanResult.completeScanResultType) {
    CompleteScanResultType.Completed,
    CompleteScanResultType.Reset -> SucceededView(completeScanResult)
    CompleteScanResultType.Failed -> FailedView(completeScanResult)
    CompleteScanResultType.Idled -> IdledView()
  }
}

@Composable
private fun SucceededView(
  completeScanResult: CompleteScanResult,
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = LocalCustomColorScheme.current.successContainer),
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 24.dp)
  ) {
    val fontSizeMedium = 16
    val fontSizeLarge = 20
    val lineHeightMedium = 20
    val lineHeightLarge = 24

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
          Icons.Outlined.Done,
          contentDescription = null,
          tint = LocalCustomColorScheme.current.onSuccessContainer,
        )

        Text(
          "Result",
          fontSize = fontSizeLarge.sp.nonScaledSp,
          lineHeight = lineHeightLarge.sp.nonScaledSp,
          style = MaterialTheme.typography.titleMedium,
          color = LocalCustomColorScheme.current.onSuccessContainer,
        )
      }

      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth(),
      ) {
        Text(
          completeScanResult.itemTagData.queueNumber,
          style = MaterialTheme.typography.displaySmall,
          color = LocalCustomColorScheme.current.onSuccessContainer,
        )
      }

      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth(),
      ) {
        when (completeScanResult.completeScanResultType) {
          CompleteScanResultType.Completed -> CompletedTag()
          else -> IdlingTag()
        }
      }

      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth(),
      ) {
        if (completeScanResult.completeScanResultType == CompleteScanResultType.Reset) {
          Text(
            completeScanResult.completeScanResultType.title,
            fontSize = fontSizeLarge.sp.nonScaledSp,
            lineHeight = lineHeightLarge.sp.nonScaledSp,
            color = LocalCustomColorScheme.current.onSuccessContainer,
          )
        }
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
          completeScanResult.itemTagInfoFromNdefMessage.scannedAt.cardTimeAgoInWordsDateString(),
          fontSize = fontSizeLarge.sp.nonScaledSp,
          lineHeight = lineHeightLarge.sp.nonScaledSp,
          color = LocalCustomColorScheme.current.onSuccessContainer,
          style = MaterialTheme.typography.titleMedium
        )
        Text(
          "complete scanned",
          fontSize = fontSizeMedium.sp.nonScaledSp,
          lineHeight = lineHeightMedium.sp.nonScaledSp,
          color = LocalCustomColorScheme.current.onSuccessContainer,
        )
      }
    }
  }
}

@Composable
private fun FailedView(
  completeScanResult: CompleteScanResult,
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
          completeScanResult.message,
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
    colors = CardDefaults.cardColors(containerColor = LocalCustomColorScheme.current.successContainer),
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
          Icons.Outlined.Done,
          contentDescription = null,
          tint = LocalCustomColorScheme.current.onSuccessContainer,
        )

        Text(
          "Result",
          style = MaterialTheme.typography.titleMedium,
          color = LocalCustomColorScheme.current.onSuccessContainer,
        )
      }
    }
  }
}
