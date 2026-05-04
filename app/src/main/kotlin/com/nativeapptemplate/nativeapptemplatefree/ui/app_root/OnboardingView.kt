package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.colorResource
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
  val brandNavy = colorResource(R.color.ic_launcher_background)

  Scaffold(
    topBar = { TopAppBar(onStartClick, brandNavy) },
    containerColor = brandNavy,
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Spacer(Modifier.weight(1f))
      Icon(
        imageVector = Icons.Filled.AutoAwesome,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(220.dp),
      )
      Spacer(Modifier.height(24.dp))
      Text(
        text = stringResource(R.string.welcome_to_app, stringResource(R.string.app_name)),
        color = Color.White,
        fontSize = 34.sp.nonScaledSp,
        lineHeight = 41.sp.nonScaledSp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
      )
      Spacer(Modifier.weight(1f))
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
  onStartClick: () -> Unit,
  containerColor: Color,
) {
  val context = LocalContext.current

  CenterAlignedTopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = containerColor,
      titleContentColor = Color.White,
      navigationIconContentColor = Color.White,
      actionIconContentColor = Color.White,
    ),
    title = { Text("") },
    actions = {
      TextButton(
        onClick = { onStartClick() },
      ) {
        Text(
          "Start",
          color = Color.White,
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
          color = Color.White,
        )
      }
    },

    modifier = Modifier.fillMaxWidth(),
  )
}
