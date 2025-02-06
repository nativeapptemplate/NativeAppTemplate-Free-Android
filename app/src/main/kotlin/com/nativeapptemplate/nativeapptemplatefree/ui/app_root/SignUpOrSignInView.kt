package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.nativeapptemplate.nativeapptemplatefree.NatConstants
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.ui.common.MainButtonView

@Composable
internal fun SignUpOrSignInView(
  onSignUpClick: () -> Unit,
  onSignInClick: () -> Unit,
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = { TopAppBar(
      onBackClick = onBackClick
    ) },
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(24.dp),
      modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(padding)
        .padding(24.dp)
    ) {
      Text(
        stringResource(R.string.app_name),
        style = MaterialTheme.typography.displaySmall,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
      )

      Image(
        painter = painterResource(R.drawable.ic_overview1_slim),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 24.dp)
      )

      val agreement = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
          append("By signing up or signing in, you agree to the ")
        }

        withLink(
          LinkAnnotation.Url(
            NatConstants.TERMS_OF_USE_URL,
            TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary))
          )
        ) {
          append(stringResource(R.string.terms_of_use))
        }

        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
          append(" and ")
        }

        withLink(
          LinkAnnotation.Url(
            NatConstants.PRIVACY_POLICY_URL,
            TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary))
          )
        ) {
          append(stringResource(R.string.privacy_policy))
        }

        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
          append(".")
        }
      }

      Text(
        agreement,
        style = MaterialTheme.typography.titleMedium,
      )

      MainButtonView(
        title = stringResource(R.string.sign_up_for_an_account),
        onClick = { onSignUpClick() },
        modifier =  Modifier
          .padding(horizontal = 24.dp)
      )

      Text(
        "or",
        style = MaterialTheme.typography.titleLarge,
      )

      TextButton(
        onClick = {onSignInClick() },
      ) {
        Text(
          stringResource(R.string.sign_in_to_your_account),
          color = MaterialTheme.colorScheme.primary,
          style = MaterialTheme.typography.titleLarge
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
  onBackClick: () -> Unit,
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
        onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(NatConstants.SUPPORT_WEBSITE_URL))) },
      ) {
        Text(
          stringResource(R.string.support_website),
        )
      }
    },
    navigationIcon = {
      IconButton(onClick = {
        onBackClick()
      }) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
      }

    },
    modifier = Modifier.fillMaxWidth(),
  )
}
