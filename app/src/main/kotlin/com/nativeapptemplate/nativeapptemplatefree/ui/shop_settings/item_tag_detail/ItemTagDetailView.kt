package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagState
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ErrorView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.MainButtonView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NativeAppTemplateAlertDialog
import com.nativeapptemplate.nativeapptemplatefree.ui.common.SnackbarMessageEffect
import com.nativeapptemplate.nativeapptemplatefree.ui.common.tags.CompletedTag
import com.nativeapptemplate.nativeapptemplatefree.ui.common.tags.IdlingTag
import com.nativeapptemplate.nativeapptemplatefree.utils.DateUtility.cardDateTimeString

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
    NativeAppTemplateAlertDialog(
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
    NativeAppTemplateAlertDialog(
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
          .padding(horizontal = 16.dp, vertical = 16.dp)
          .verticalScroll(rememberScrollState()),
      ) {
        HeaderRow(itemTag = uiState.itemTag)
        DescriptionSection(description = uiState.itemTag.getDescription())
        CompletedAtRow(itemTag = uiState.itemTag)
        StateToggleButton(viewModel = viewModel, uiState = uiState)
      }
    }
  }
}

@Composable
private fun HeaderRow(itemTag: ItemTag) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Text(
      itemTag.getName(),
      style = MaterialTheme.typography.titleLarge,
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.weight(1f),
    )

    when (itemTag.getData()?.getItemTagState()) {
      ItemTagState.Completed -> CompletedTag()
      ItemTagState.Idled -> IdlingTag()
      null -> Unit
    }
  }
}

@Composable
private fun DescriptionSection(description: String) {
  if (description.isBlank()) return
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Text(
      stringResource(R.string.description_label),
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      description,
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}

@Composable
private fun CompletedAtRow(itemTag: ItemTag) {
  if (itemTag.getData()?.getItemTagState() != ItemTagState.Completed) return
  val completedAt = itemTag.getCompletedAt()
  if (completedAt.isNullOrBlank()) return

  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.fillMaxWidth(),
  ) {
    Text(
      stringResource(R.string.completed_at_label),
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      completedAt.cardDateTimeString(),
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}

@Composable
private fun StateToggleButton(
  viewModel: ItemTagDetailViewModel,
  uiState: ItemTagDetailUiState,
) {
  val state = uiState.itemTag.getData()?.getItemTagState() ?: return

  val title = when (state) {
    ItemTagState.Idled -> stringResource(R.string.mark_as_completed)
    ItemTagState.Completed -> stringResource(R.string.mark_as_idled)
  }
  val onClick: () -> Unit = when (state) {
    ItemTagState.Idled -> viewModel::completeItemTag
    ItemTagState.Completed -> viewModel::idleItemTag
  }

  MainButtonView(
    title = title,
    onClick = onClick,
    enabled = !uiState.isToggling,
    color = MaterialTheme.colorScheme.primary,
    titleColor = MaterialTheme.colorScheme.primary,
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 16.dp),
  )
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
