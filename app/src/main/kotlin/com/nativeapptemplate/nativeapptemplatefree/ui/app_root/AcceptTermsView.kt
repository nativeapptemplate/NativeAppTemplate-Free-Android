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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nativeapptemplate.nativeapptemplatefree.NativeAppTemplateConstants
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.ui.common.FormatLink
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.MainButtonType
import com.nativeapptemplate.nativeapptemplatefree.ui.common.MainButtonView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NativeAppTemplateAlertDialog
import com.nativeapptemplate.nativeapptemplatefree.ui.common.SnackbarMessageEffect
import com.nativeapptemplate.nativeapptemplatefree.ui.common.linkedFormat
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility.restartApp

@Composable
fun AcceptTermsView(
  viewModel: AcceptTermsViewModel = hiltViewModel(),
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  SnackbarMessageEffect(
    message = uiState.message,
    onShowSnackbar = onShowSnackbar,
    onMessageShown = viewModel::snackbarMessageShown,
  )

  if (uiState.isUpdated) {
    val context = LocalContext.current
    NativeAppTemplateAlertDialog(
      dialogTitle = stringResource(R.string.confirmed_terms_version_updated),
      onDismissRequest = { context.restartApp() },
    )
  }

  AcceptTermsView(
    viewModel,
    uiState,
  )
}

@Composable
fun AcceptTermsView(
  viewModel: AcceptTermsViewModel,
  uiState: AcceptTermsUiState,
) {
  ContentView(viewModel, uiState)
}

@Composable
private fun ContentView(
  viewModel: AcceptTermsViewModel,
  uiState: AcceptTermsUiState,
) {
  if (uiState.isLoading) {
    AcceptTermsLoadingView()
  } else {
    AcceptTermsContentView(viewModel)
  }
}

@Composable
fun AcceptTermsContentView(
  viewModel: AcceptTermsViewModel,
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
      verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
      val annotatedString = linkedFormat(
        format = stringResource(R.string.please_accept_updated),
        links = listOf(FormatLink(stringResource(R.string.terms_of_use), NativeAppTemplateConstants.TERMS_OF_USE_URL)),
        textStyle = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant),
        linkStyle = SpanStyle(color = MaterialTheme.colorScheme.primary),
      )

      Text(
        annotatedString,
        style = MaterialTheme.typography.titleLarge,
      )

      MainButtonView(
        title = stringResource(R.string.accept),
        type = MainButtonType.PRIMARY,
        onClick = { viewModel.updateConfirmedTermsVersion() },
        modifier = Modifier
          .padding(horizontal = 12.dp, vertical = 24.dp),
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar() {
  CenterAlignedTopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      titleContentColor = MaterialTheme.colorScheme.primary,
    ),
    title = { Text(stringResource(R.string.terms_of_use_updated)) },
    modifier = Modifier.fillMaxWidth(),
  )
}

@Composable
private fun AcceptTermsLoadingView() {
  Scaffold(
    topBar = {
      TopAppBar()
    },
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(padding),
      contentAlignment = Alignment.Center,
    ) {
      LoadingView()
    }
  }
}
