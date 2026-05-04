package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativeapptemplate.nativeapptemplatefree.NativeAppTemplateConstants
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NonScaledSp.nonScaledSp

@Composable
internal fun OnboardingView(
  onStartClick: () -> Unit,
) {
  Scaffold(
    topBar = { TopAppBar(onStartClick) },
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(12.dp)
        .padding(top = 12.dp),
    ) {
      Icon(
        imageVector = Icons.Filled.AutoAwesome,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier
          .align(Alignment.TopCenter)
          .fillMaxSize()
          .padding(bottom = 192.dp),
      )
      Box(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.background),
      ) {
        Text(
          text = stringResource(R.string.welcome_to_app, stringResource(R.string.app_name)),
          color = MaterialTheme.colorScheme.onBackground,
          fontSize = 34.sp.nonScaledSp,
          lineHeight = 41.sp.nonScaledSp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center,
          modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .padding(16.dp),
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
  onStartClick: () -> Unit,
) {
  val context = LocalContext.current

  CenterAlignedTopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      titleContentColor = MaterialTheme.colorScheme.primary,
    ),
    title = { Text("") },
    actions = {
      TextButton(
        onClick = { onStartClick() },
      ) {
        Text(
          "Start",
          style = MaterialTheme.typography.displaySmall,
        )
      }
    },
    navigationIcon = {
      TextButton(
        onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(NativeAppTemplateConstants.SUPPORT_WEBSITE_URL))) },
      ) {
        Text(
          stringResource(R.string.support_website),
        )
      }
    },

    modifier = Modifier.fillMaxWidth(),
  )
}
