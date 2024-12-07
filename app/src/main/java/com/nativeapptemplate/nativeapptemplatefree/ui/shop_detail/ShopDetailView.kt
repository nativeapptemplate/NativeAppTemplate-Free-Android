package com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ErrorView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView

@Composable
internal fun ShopDetailView(
  viewModel: ShopDetailViewModel = hiltViewModel(),
  onSettingsClick: (String) -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  val uiState: ShopDetailUiState by viewModel.uiState.collectAsStateWithLifecycle()

  LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    viewModel.reload()
  }

  LaunchedEffect(uiState.message) {
    if (uiState.message.isNotBlank()) {
      onShowSnackbar(uiState.message, "dismiss", SnackbarDuration.Indefinite)
      viewModel.snackbarMessageShown()
    }
  }

  ShopDetailView(
    viewModel = viewModel,
    uiState = uiState,
    onSettingsClick = onSettingsClick,
    onBackClick = onBackClick,
  )
}

@Composable
fun ShopDetailView(
  viewModel: ShopDetailViewModel,
  uiState: ShopDetailUiState,
  onSettingsClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  ContentView(
    viewModel = viewModel,
    uiState = uiState,
    onSettingsClick = onSettingsClick,
    onBackClick = onBackClick,
  )
}

@Composable
private fun ContentView(
  viewModel: ShopDetailViewModel,
  uiState: ShopDetailUiState,
  onSettingsClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  if (uiState.isLoading) {
    ShopDetailLoadingView(uiState, onSettingsClick, onBackClick)
  } else if (uiState.success) {
    ShopDetailContentView(
      uiState = uiState,
      onSettingsClick = onSettingsClick,
      onBackClick = onBackClick,
    )
  } else {
    ShopDetailErrorView(viewModel, uiState, onSettingsClick, onBackClick)
  }
}

@Composable
private fun ShopDetailContentView(
  uiState: ShopDetailUiState,
  onSettingsClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        uiState,
        onSettingsClick,
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
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .padding(16.dp)
      ) {
        Text(
          uiState.shop.getName(),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.displaySmall,
          modifier = Modifier
            .fillMaxWidth()
        )

        Text(
          uiState.shop.getDescription(),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.displaySmall,
          modifier = Modifier
            .fillMaxWidth()
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
  uiState: ShopDetailUiState,
  onSettingsClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  CenterAlignedTopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      titleContentColor = MaterialTheme.colorScheme.primary,
    ),
    title = { Text(text = uiState.shop.getName()) },
    navigationIcon = {
      IconButton(onClick = {
        onBackClick()
      }) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
      }
    },
    actions = {
      IconButton(
        onClick = {
          if (uiState.success) {
            onSettingsClick(uiState.shop.getData()?.id!!)
          }
        },
      ) {
        Icon(
          Icons.Filled.Settings,
          "Shop Settings",
          tint = MaterialTheme.colorScheme.onSurface,
        )
      }
    },
    modifier = Modifier.fillMaxWidth(),
  )
}

@Composable
private fun ShopDetailErrorView(
  viewModel: ShopDetailViewModel,
  uiState: ShopDetailUiState,
  onSettingsClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        uiState,
        onSettingsClick,
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
      ErrorView(onClick = viewModel::reload)
    }
  }
}

@Composable
private fun ShopDetailLoadingView(
  uiState: ShopDetailUiState,
  onSettingsClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        uiState,
        onSettingsClick,
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
