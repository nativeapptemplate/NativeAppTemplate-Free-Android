package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list

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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nativeapptemplate.nativeapptemplatefree.NativeAppTemplateConstants
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NativeAppTemplateAlertDialog
import com.nativeapptemplate.nativeapptemplatefree.ui.common.SnackbarMessageEffect
import com.nativeapptemplate.nativeapptemplatefree.ui.common.SubmitFab

@Composable
fun ItemTagCreateView(
  viewModel: ItemTagCreateViewModel = hiltViewModel(),
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  SnackbarMessageEffect(
    message = uiState.message,
    onShowSnackbar = onShowSnackbar,
    onMessageShown = viewModel::snackbarMessageShown,
  )

  if (uiState.isCreated) {
    NativeAppTemplateAlertDialog(
      dialogTitle = stringResource(R.string.message_item_tag_created),
      onDismissRequest = { onBackClick() },
    )
  }

  ItemTagCreateView(
    viewModel,
    uiState,
    onBackClick,
  )
}

@Composable
fun ItemTagCreateView(
  viewModel: ItemTagCreateViewModel,
  uiState: ItemTagCreateUiState,
  onBackClick: () -> Unit,
) {
  ContentView(viewModel, uiState, onBackClick)
}

@Composable
private fun ContentView(
  viewModel: ItemTagCreateViewModel,
  uiState: ItemTagCreateUiState,
  onBackClick: () -> Unit,
) {
  if (uiState.isLoading) {
    ItemTagCreateLoadingView(onBackClick)
  } else {
    ItemTagCreateContentView(viewModel, uiState, onBackClick)
  }
}

@Composable
fun ItemTagCreateContentView(
  viewModel: ItemTagCreateViewModel,
  uiState: ItemTagCreateUiState,
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(onBackClick)
    },
    floatingActionButton = {
      SubmitFab(
        label = stringResource(R.string.label_add_item_tag),
        onClick = { viewModel.createItemTag() },
        enabled = !viewModel.hasInvalidData(),
      )
    },
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Column(
      modifier = Modifier
        .padding(padding)
        .padding(horizontal = 16.dp, vertical = 16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      OutlinedTextField(
        label = {
          Text(
            text = stringResource(R.string.name_label),
          )
        },
        placeholder = { Text(stringResource(R.string.item_tag_name_placeholder)) },
        value = uiState.name,
        onValueChange = { viewModel.updateName(it) },
        supportingText = {
          Column {
            Text(
              text = stringResource(R.string.item_tag_name_help, uiState.maximumNameLength),
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
              text = stringResource(R.string.item_tag_name_is_invalid),
              style = MaterialTheme.typography.bodyLarge,
              color = if (viewModel.hasInvalidDataName()) Color.Red else Color.Transparent,
            )
          }
        },
        modifier = Modifier
          .fillMaxWidth(),
      )

      OutlinedTextField(
        label = {
          Text(
            text = stringResource(R.string.description_label),
          )
        },
        value = uiState.description,
        onValueChange = { viewModel.updateDescription(it) },
        minLines = 4,
        supportingText = {
          Column {
            Text(
              text = stringResource(R.string.item_tag_description_help, NativeAppTemplateConstants.MAXIMUM_ITEM_TAG_DESCRIPTION_LENGTH),
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
              text = stringResource(R.string.item_tag_description_is_invalid),
              style = MaterialTheme.typography.bodyLarge,
              color = if (viewModel.hasInvalidDataDescription()) Color.Red else Color.Transparent,
            )
          }
        },
        modifier = Modifier
          .fillMaxWidth(),
      )
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
    title = { Text(text = stringResource(id = R.string.label_add_item_tag)) },
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
private fun ItemTagCreateLoadingView(
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
      contentAlignment = Alignment.Center,
    ) {
      LoadingView()
    }
  }
}
