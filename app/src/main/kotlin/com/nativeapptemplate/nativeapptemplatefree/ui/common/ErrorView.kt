package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CrisisAlert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.NatTheme

@Composable
fun ErrorView(
  titleText: String = "Something went wrong.",
  bodyText: String = "Please try again.",
  buttonTitle: String = "Reload",
  onClick: () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(24.dp),
    modifier = Modifier.padding(16.dp)
  ) {
    Icon(
      Icons.Outlined.CrisisAlert,
      contentDescription = null,
      modifier = Modifier.size(128.dp)
    )

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier
        .padding(horizontal = 16.dp)
    ) {
      Text(
        titleText,
        style = MaterialTheme.typography.headlineSmall,
      )

      Text(bodyText)
    }

    MainButtonView(
      title = buttonTitle,
      onClick = { onClick() },
      modifier =  Modifier
        .padding(horizontal = 12.dp, vertical = 24.dp)
    )
  }
}

@Preview
@Composable
private fun LoadingStatePreview() {
  NatTheme {
    ErrorView(
      onClick = {},
    )
  }
}
