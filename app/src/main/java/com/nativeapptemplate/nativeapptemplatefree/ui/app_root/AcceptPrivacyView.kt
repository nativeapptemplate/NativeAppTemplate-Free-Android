package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nativeapptemplate.nativeapptemplatefree.NatConstants
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.MainButtonView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NatAlertDialog
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility.restartApp

@Composable
fun AcceptPrivacyView(
  viewModel: AcceptPrivacyViewModel = hiltViewModel(),
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(uiState.message) {
    if (uiState.message.isNotBlank()) {
      onShowSnackbar(uiState.message, "dismiss", SnackbarDuration.Indefinite)
      viewModel.snackbarMessageShown()
    }
  }

  if (uiState.isUpdated) {
    val context = LocalContext.current
    NatAlertDialog(
      dialogTitle= stringResource(R.string.confirmed_privacy_version_updated),
      onDismissRequest = { context.restartApp() },
    )
  }

  AcceptPrivacyView(
    viewModel,
    uiState,
  )
}

@Composable
fun AcceptPrivacyView(
  viewModel: AcceptPrivacyViewModel,
  uiState: AcceptPrivacyUiState,
) {
  ContentView(viewModel, uiState)
}

@Composable
private fun ContentView(
  viewModel: AcceptPrivacyViewModel,
  uiState: AcceptPrivacyUiState,
) {
  if (uiState.isLoading) {
    AcceptPrivacyLoadingView()
  } else {
    AcceptPrivacyContentView(viewModel)
  }
}

@Composable
fun AcceptPrivacyContentView(
  viewModel: AcceptPrivacyViewModel,
) {
  Scaffold(
    topBar = {
      TopAppBar()
    },
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Column(
      modifier = Modifier
        .padding(padding)
        .padding(horizontal = 24.dp, vertical = 16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
      val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
          append("Please accept updated")
          append(" ")
        }

        withLink(
          LinkAnnotation.Url(
            NatConstants.PRIVACY_POLICY_URL,
            TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary))
          )
        ) {
          append(stringResource(R.string.privacy_policy))
        }

        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
          append(".")
        }
      }

      Text(
        annotatedString,
        style = MaterialTheme.typography.titleLarge,
      )

      MainButtonView(
        title = stringResource(R.string.accept),
        onClick = { viewModel.updateConfirmedPrivacyVersion() },
        modifier =  Modifier
          .padding(horizontal = 12.dp, vertical = 24.dp)
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
) {
  CenterAlignedTopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      titleContentColor = MaterialTheme.colorScheme.primary,
    ),
    title = { Text(stringResource(R.string.privacy_policy_updated)) },
    modifier = Modifier.fillMaxWidth(),
  )
}

@Composable
private fun AcceptPrivacyLoadingView(
) {
  Scaffold(
    topBar = {
      TopAppBar(
      )
    },
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(padding),
      contentAlignment = Alignment.Center
    ) {
      LoadingView()
    }
  }
}
