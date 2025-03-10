package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ErrorView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NatAlertDialog

@Composable
fun ItemTagCreateView(
  viewModel: ItemTagCreateViewModel = hiltViewModel(),
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    viewModel.reload()
  }

  LaunchedEffect(uiState.message) {
    if (uiState.message.isNotBlank()) {
      onShowSnackbar(uiState.message, "dismiss", SnackbarDuration.Indefinite)
      viewModel.snackbarMessageShown()
    }
  }

  if (uiState.isCreated) {
    NatAlertDialog(
      dialogTitle= stringResource(R.string.message_item_tag_created),
      onDismissRequest = { onBackClick() },
    )
  }

  ItemTagCreateView(
    viewModel,
    uiState,
    onBackClick
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
  } else if (uiState.success) {
    ItemTagCreateContentView(viewModel, uiState, onBackClick)
  } else {
    ItemTagCreateErrorView(viewModel, onBackClick)
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
      // FloatingActionButton doesn't support the enabled property
      // https://stackoverflow.com/a/68853697/1160200
      Button(
        onClick = { viewModel.createItemTag() },
        modifier = Modifier.defaultMinSize(minWidth = 64.dp, minHeight = 64.dp),
        enabled = !viewModel.hasInvalidData(),
        shape = CircleShape

      ){
        Icon(Icons.Filled.Done, contentDescription = stringResource(R.string.label_add_tag))
      }
    },
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Column(
      modifier = Modifier
        .padding(padding)
        .padding(horizontal = 16.dp, vertical = 16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      OutlinedTextField(
        label = {
          Text(
            text = stringResource(R.string.tag_number)
          )
        },
        placeholder = { Text("A001") },
        value = uiState.queueNumber,
        onValueChange = { viewModel.updateQueueNumber(it) },
        supportingText = {
          Column {
            Text(
              text = "Tag Number must be a 2-${uiState.maximumQueueNumberLength} alphanumeric characters.",
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = stringResource(R.string.zero_padding),
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = stringResource(id = R.string.tag_number_is_invalid),
              style = MaterialTheme.typography.bodyLarge,
              color = if (viewModel.hasInvalidDataQueueNumber()) Color.Red else Color.Transparent
            )
          }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
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
    title = { Text(text = stringResource(id = R.string.label_add_tag)) },
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
private fun ItemTagCreateErrorView(
  viewModel: ItemTagCreateViewModel,
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
      contentAlignment = Alignment.Center
    ) {
      LoadingView()
    }
  }
}
