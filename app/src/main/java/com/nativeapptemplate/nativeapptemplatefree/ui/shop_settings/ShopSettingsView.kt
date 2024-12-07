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
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ErrorView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.MainButtonView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NatAlertDialog
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility.restartApp

@Composable
internal fun ShopSettingsView(
  viewModel: ShopSettingsViewModel = hiltViewModel(),

  onShowBasicSettingsClick: (String) -> Unit,

  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  val uiState: ShopSettingsUiState by viewModel.uiState.collectAsStateWithLifecycle()

  LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    viewModel.reload()
  }

  LaunchedEffect(uiState.message) {
    if (uiState.message.isNotBlank()) {
      onShowSnackbar(uiState.message, "dismiss", SnackbarDuration.Indefinite)
      viewModel.snackbarMessageShown()
    }
  }

  if (uiState.isShopDeleted) {
    val context = LocalContext.current
    context.restartApp()
  }

  ShopSettingsView(
    viewModel = viewModel,
    uiState = uiState,

    onShowBasicSettingsClick = onShowBasicSettingsClick,

    onBackClick = onBackClick,
  )
}

@Composable
fun ShopSettingsView(
  viewModel: ShopSettingsViewModel,
  uiState: ShopSettingsUiState,

  onShowBasicSettingsClick: (String) -> Unit,

  onBackClick: () -> Unit,
) {
  ContentView(
    viewModel = viewModel,
    uiState = uiState,

    onShowBasicSettingsClick = onShowBasicSettingsClick,

    onBackClick = onBackClick,
  )
}

@Composable
private fun ContentView(
  viewModel: ShopSettingsViewModel,
  uiState: ShopSettingsUiState,

  onShowBasicSettingsClick: (String) -> Unit,

  onBackClick: () -> Unit,
) {
  if (uiState.isLoading) {
    ShopSettingsLoadingView(onBackClick)
  } else if (uiState.success) {
    ShopSettingsContentView(
      viewModel = viewModel,
      uiState = uiState,

      onShowBasicSettingsClick = onShowBasicSettingsClick,

      onBackClick = onBackClick,
    )
  } else {
    ShopSettingsErrorView(viewModel, onBackClick)
  }
}

@Composable
private fun ShopSettingsContentView(
  viewModel: ShopSettingsViewModel,
  uiState: ShopSettingsUiState,

  onShowBasicSettingsClick: (String) -> Unit,

  onBackClick: () -> Unit,
) {
  var isShowingDeleteConfirmationDialog by remember { mutableStateOf(false) }

  if (isShowingDeleteConfirmationDialog) {
    NatAlertDialog(
      dialogTitle= stringResource(R.string.are_you_sure),
      confirmButtonTitle = stringResource(R.string.title_delete_shop),
      onDismissRequest = { isShowingDeleteConfirmationDialog = false },
      onConfirmation = { viewModel.deleteShop(uiState.shop.getData()?.id!!) },
      icon = Icons.Outlined.AddAlert,
    )
  }

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
              .fillMaxWidth()
              .padding(bottom = 48.dp)
          )
        }

        item {
          HorizontalDivider()

          ListItem(
            headlineContent = {
              Text(
                stringResource(R.string.label_shop_settings_basic_settings),
                style = MaterialTheme.typography.titleMedium
              )
            },
            leadingContent = {
              Icon(
                Icons.Outlined.Storefront,
                contentDescription = stringResource(R.string.label_shop_settings_basic_settings),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            },
            modifier = Modifier
              .clickable { onShowBasicSettingsClick(uiState.shop.getData()?.id!!) }
          )

          HorizontalDivider()
        }

        item {
          ListItem(
            headlineContent = {
              Text(
                "",
                style = MaterialTheme.typography.titleMedium
              )
            },
          )
        }

        item {
          MainButtonView(
            title = stringResource(R.string.title_delete_shop),
            onClick = { isShowingDeleteConfirmationDialog = true },
            modifier =  Modifier
              .padding(horizontal = 12.dp, vertical = 24.dp)
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
    title = { Text(stringResource(R.string.label_shop_settings)) },
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
private fun ShopSettingsErrorView(
  viewModel: ShopSettingsViewModel,
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        onBackClick = onBackClick,
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
      ErrorView(
        onClick = { viewModel.reload() }
      )
    }
  }
}

@Composable
private fun ShopSettingsLoadingView(
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
