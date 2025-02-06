package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.NatTheme
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility

@Composable
fun NeedAppUpdatesView(
) {
  val context = LocalContext.current

  Scaffold(
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(padding),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .padding(16.dp)
      ) {
        Icon(
          Icons.Outlined.Update,
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
            stringResource(R.string.update_app),
            style = MaterialTheme.typography.titleLarge,
          )

          Text(stringResource(R.string.install_new_version_app))
        }

        TextButton(
          onClick = {
            try {
              context.startActivity(Intent(Intent.ACTION_VIEW, Utility.marketUri()))
            } catch (e: ActivityNotFoundException) {
              context.startActivity(Intent(Intent.ACTION_VIEW, Utility.googlePlayStoreUri()))
            }
          },
        ) {
          Text(
            stringResource(R.string.update_app),
            style = MaterialTheme.typography.titleMedium
          )
        }
      }
    }
  }
}

@Preview
@Composable
private fun LoadingStatePreview() {
  NatTheme {
    NeedAppUpdatesView(
    )
  }
}
