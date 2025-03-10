package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Web
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nativeapptemplate.nativeapptemplatefree.NatConstants
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ErrorView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView

@Composable
internal fun NumberTagsWebpageListView(
  viewModel: NumberTagsWebpageListViewModel = hiltViewModel(),
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  val uiState: NumberTagsWebpageListUiState by viewModel.uiState.collectAsStateWithLifecycle()

  LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    viewModel.reload()
  }

  LaunchedEffect(uiState.message) {
    if (uiState.message.isNotBlank()) {
      onShowSnackbar(uiState.message, "dismiss", SnackbarDuration.Indefinite)
      viewModel.snackbarMessageShown()
    }
  }

  NumberTagsWebpageListView(
    viewModel = viewModel,
    uiState = uiState,
    onBackClick = onBackClick,
  )
}

@Composable
fun NumberTagsWebpageListView(
  viewModel: NumberTagsWebpageListViewModel,
  uiState: NumberTagsWebpageListUiState,
  onBackClick: () -> Unit,
) {
  ContentView(
    viewModel = viewModel,
    uiState = uiState,
    onBackClick = onBackClick,
  )
}

@Composable
private fun ContentView(
  viewModel: NumberTagsWebpageListViewModel,
  uiState: NumberTagsWebpageListUiState,
  onBackClick: () -> Unit,
) {
  if (uiState.isLoading) {
    NumberTagsWebpageListLoadingView(onBackClick)
  } else if (uiState.success) {
    NumberTagsWebpageListContentView(
      uiState = uiState,
      onBackClick = onBackClick,
    )
  } else {
    NumberTagsWebpageListErrorView(viewModel, onBackClick)
  }
}

@Composable
private fun NumberTagsWebpageListContentView(
  uiState: NumberTagsWebpageListUiState,
  onBackClick: () -> Unit,
) {
  val localClipboardManager = LocalClipboardManager.current

  Scaffold(
    topBar = {
      TopAppBar(
        onBackClick,
      )
    },
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(padding)
    ) {
      LazyColumn(
        Modifier.padding(24.dp)
      ) {
        item {
          Text(
            uiState.shop.getName(),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
              .fillMaxWidth(),
          )
        }

        item {
          ListItem(
            headlineContent = {
              Text(
                stringResource(R.string.server_number_tags_webpage),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
              )
            },
            leadingContent = {
              Icon(
                Icons.Outlined.Web,
                contentDescription = stringResource(R.string.label_shop_settings_number_tags_webpage),
                tint = MaterialTheme.colorScheme.primary,
              )
            },
            modifier = Modifier
              .clickable {
                localClipboardManager.setText(AnnotatedString(uiState.shop.displayShopServerUrlString(NatConstants.baseUrlString())))
              },
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
  onBackClick: () -> Unit,
) {
  CenterAlignedTopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      titleContentColor = MaterialTheme.colorScheme.primary,
    ),
    title = { Text(stringResource(R.string.label_shop_settings_number_tags_webpage)) },
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

@Composable
private fun NumberTagsWebpageListErrorView(
  viewModel: NumberTagsWebpageListViewModel,
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        onBackClick
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
      ErrorView(onClick = viewModel::reload)
    }
  }
}

@Composable
private fun NumberTagsWebpageListLoadingView(
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        onBackClick,
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
