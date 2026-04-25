package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NatAlertDialog
import com.nativeapptemplate.nativeapptemplatefree.ui.common.SnackbarMessageEffect

@Composable
internal fun ItemTagDetailView(
  viewModel: ItemTagDetailViewModel = hiltViewModel(),
  onShowItemTagEditClick: (String) -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  val uiState: ItemTagDetailUiState by viewModel.uiState.collectAsStateWithLifecycle()

  LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
    viewModel.reload()
  }

  SnackbarMessageEffect(
    message = uiState.message,
    onShowSnackbar = onShowSnackbar,
    onMessageShown = viewModel::snackbarMessageShown,
  )

  if (uiState.isDeleted) {
    NatAlertDialog(
      dialogTitle = stringResource(R.string.message_item_tag_deleted),
      onDismissRequest = { onBackClick() },
    )
  }

  ItemTagDetailView(
    viewModel = viewModel,
    uiState = uiState,
    onShowItemTagEditClick = onShowItemTagEditClick,
    onBackClick = onBackClick,
  )
}

@Composable
fun ItemTagDetailView(
  viewModel: ItemTagDetailViewModel,
  uiState: ItemTagDetailUiState,
  onShowItemTagEditClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  ContentView(
    viewModel = viewModel,
    uiState = uiState,
    onShowItemTagEditClick = onShowItemTagEditClick,
    onBackClick = onBackClick,
  )
}

@Composable
private fun ContentView(
  viewModel: ItemTagDetailViewModel,
  uiState: ItemTagDetailUiState,
  onShowItemTagEditClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  if (uiState.isLoading) {
    ItemTagDetailLoadingView(
      viewModel = viewModel,
      uiState = uiState,
      onBackClick = onBackClick,
    )
  } else if (uiState.success) {
    ItemTagDetailContentView(
      viewModel = viewModel,
      uiState = uiState,
      onShowItemTagEditClick = onShowItemTagEditClick,
      onBackClick = onBackClick,
    )
  } else {
    ItemTagDetailErrorView(
      viewModel = viewModel,
      uiState = uiState,
      onBackClick = onBackClick,
    )
  }
}

@Composable
private fun ItemTagDetailContentView(
  viewModel: ItemTagDetailViewModel,
  uiState: ItemTagDetailUiState,
  onShowItemTagEditClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  var isShowingDeleteConfirmationDialog by remember { mutableStateOf(false) }

  if (isShowingDeleteConfirmationDialog) {
    NatAlertDialog(
      dialogTitle = stringResource(R.string.are_you_sure),
      confirmButtonTitle = stringResource(R.string.title_delete_item_tag),
      onDismissRequest = { isShowingDeleteConfirmationDialog = false },
      onConfirmation = { viewModel.deleteItemTag() },
      icon = Icons.Outlined.AddAlert,
    )
  }

  Scaffold(
    topBar = {
      TopAppBar(
        viewModel = viewModel,
        onShowItemTagEditClick = onShowItemTagEditClick,
        onDeleteItemTagClick = { isShowingDeleteConfirmationDialog = true },
        uiState = uiState,
        onBackClick = onBackClick,
      )
    },
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(padding),
    ) {
      Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
          .padding(horizontal = 16.dp, vertical = 16.dp)
          .verticalScroll(rememberScrollState()),
      ) {
        Text(
          uiState.itemTag.getName(),
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.primary,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth(),
        )

        Text(
          uiState.itemTag.getDescription(),
          style = MaterialTheme.typography.bodyMedium,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth(),
        )

        Text(
          uiState.itemTag.getState(),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth(),
        )

        uiState.itemTag.getCompletedAt()?.takeIf { it.isNotBlank() }?.let { completedAt ->
          Text(
            completedAt,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
  viewModel: ItemTagDetailViewModel,
  uiState: ItemTagDetailUiState,
  onShowItemTagEditClick: (String) -> Unit,
  onDeleteItemTagClick: () -> Unit,
  onBackClick: () -> Unit,
) {
  CenterAlignedTopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      titleContentColor = MaterialTheme.colorScheme.primary,
    ),
    title = {},
    navigationIcon = {
      IconButton(onClick = {
        onBackClick()
      }) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
      }
    },
    actions = {
      if (uiState.success) {
        TextButton(
          onClick = { onShowItemTagEditClick(viewModel.itemTagId) },
        ) {
          Text(
            "Edit",
            color = MaterialTheme.colorScheme.onSurface,
          )
        }

        IconButton(
          onClick = { onDeleteItemTagClick() },
        ) {
          Icon(
            Icons.Filled.Delete,
            "Delete",
            tint = MaterialTheme.colorScheme.onSurface,
          )
        }
      }
    },
    modifier = Modifier.fillMaxWidth(),
  )
}

@Composable
private fun ItemTagDetailErrorView(
  viewModel: ItemTagDetailViewModel,
  uiState: ItemTagDetailUiState,
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        viewModel = viewModel,
        uiState = uiState,
        onShowItemTagEditClick = {},
        onDeleteItemTagClick = {},
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
      contentAlignment = Alignment.Center,
    ) {
      ErrorView(
        onClick = { viewModel.reload() },
      )
    }
  }
}

@Composable
private fun ItemTagDetailLoadingView(
  viewModel: ItemTagDetailViewModel,
  uiState: ItemTagDetailUiState,
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        viewModel = viewModel,
        uiState = uiState,
        onShowItemTagEditClick = {},
        onDeleteItemTagClick = {},
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
      contentAlignment = Alignment.Center,
    ) {
      LoadingView()
    }
  }
}
