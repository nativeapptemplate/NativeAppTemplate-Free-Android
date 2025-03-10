package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail

import android.nfc.NfcAdapter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lightspark.composeqr.QrCodeView
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagType
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ErrorView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.MainButtonView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NatAlertDialog
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NonScaledSp.nonScaledSp
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility.shareImage
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch

@Composable
internal fun ItemTagDetailView(
  viewModel: ItemTagDetailViewModel = hiltViewModel(),
  onShowItemTagEditClick: (String) -> Unit,
  onShowItemTagWriteClick: (String, Boolean, String) -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  val uiState: ItemTagDetailUiState by viewModel.uiState.collectAsStateWithLifecycle()

  LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
    viewModel.reload()
  }

  LaunchedEffect(uiState.message) {
    if (uiState.message.isNotBlank()) {
      onShowSnackbar(uiState.message, "dismiss", SnackbarDuration.Indefinite)
      viewModel.snackbarMessageShown()
    }
  }

  if (uiState.isDeleted) {
    NatAlertDialog(
      dialogTitle= stringResource(R.string.message_item_tag_deleted),
      onDismissRequest = { onBackClick() },
    )
  }

  ItemTagDetailView(
    viewModel = viewModel,
    uiState = uiState,
    onShowItemTagEditClick = onShowItemTagEditClick,
    onShowItemTagWriteClick = onShowItemTagWriteClick,
    onBackClick = onBackClick,
  )
}

@Composable
fun ItemTagDetailView(
  viewModel: ItemTagDetailViewModel,
  uiState: ItemTagDetailUiState,
  onShowItemTagEditClick: (String) -> Unit,
  onShowItemTagWriteClick: (String, Boolean, String) -> Unit,
  onBackClick: () -> Unit,
) {
  ContentView(
    viewModel = viewModel,
    uiState = uiState,
    onShowItemTagEditClick = onShowItemTagEditClick,
    onShowItemTagWriteClick = onShowItemTagWriteClick,
    onBackClick = onBackClick,
  )
}

@Composable
private fun ContentView(
  viewModel: ItemTagDetailViewModel,
  uiState: ItemTagDetailUiState,
  onShowItemTagEditClick: (String) -> Unit,
  onShowItemTagWriteClick: (String, Boolean, String) -> Unit,
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
      onShowItemTagWriteClick = onShowItemTagWriteClick,
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ItemTagDetailContentView(
  viewModel: ItemTagDetailViewModel,
  uiState: ItemTagDetailUiState,
  onShowItemTagEditClick: (String) -> Unit,
  onShowItemTagWriteClick: (String, Boolean, String) -> Unit,
  onBackClick: () -> Unit,
) {
  val context = LocalContext.current
  var isShowingDeleteConfirmationDialog by remember { mutableStateOf(false) }
  var isLocked by remember { mutableStateOf(false) }
  val doesDeviceSupportTagScanning = NfcAdapter.getDefaultAdapter(context) != null
  val deviceDoesNotSupportTagScanningMessage = stringResource(R.string.this_device_does_not_support_tag_scanning)

  if (isShowingDeleteConfirmationDialog) {
    NatAlertDialog(
      dialogTitle= stringResource(R.string.are_you_sure),
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
        .padding(padding)
    ) {
      Column(
        modifier = Modifier
          .padding(horizontal = 16.dp, vertical = 16.dp)
          .verticalScroll(rememberScrollState()),
      ) {
        Text(
          "Write Info to Tag / Save Customer QR code",
          style = MaterialTheme.typography.titleMedium,
          textAlign = TextAlign.Center,
          modifier = Modifier
            .fillMaxWidth(),
        )

        Text(
          uiState.itemTag.getShopName(),
          style = MaterialTheme.typography.titleSmall,
          textAlign = TextAlign.Center,
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
        )

        Text(
          uiState.itemTag.getQueueNumber(),
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.primary,
          textAlign = TextAlign.Center,
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
        )

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
        ) {
          Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
              .padding(24.dp)
          ) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                Icons.Outlined.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiary,
              )

              Text(
                "Lock",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiary,
              )
            }
            Row(
              horizontalArrangement = Arrangement
                .spacedBy(
                  space = 8.dp,
                  alignment = Alignment.CenterHorizontally,
                ),
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth(),
            ) {
              Text(
                "Lock",
                color = MaterialTheme.colorScheme.onTertiary,
              )

              Switch(
                checked = isLocked,
                onCheckedChange = {
                  isLocked = it
                  viewModel.updateIsLock(it)
                },
              )
            }
            if (isLocked) {
              Text(
                stringResource(R.string.you_cannot_undo_after_locking_tag),
                color = MaterialTheme.colorScheme.onError,
              )
            }
          }
        }

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error),
          modifier = Modifier
            .padding(top = 24.dp)
        ) {
          Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
              .padding(24.dp)
          ) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                Icons.Outlined.Storefront,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onError,
              )

              Text(
                "Server",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onError,
              )
            }
            MainButtonView(
              title = stringResource(R.string.write_server_tag),
              onClick = {
                if (doesDeviceSupportTagScanning) {
                  onShowItemTagWriteClick(viewModel.itemTagId, uiState.isLock, ItemTagType.Server.param)
                } else {
                  viewModel.updateMessage(deviceDoesNotSupportTagScanningMessage)
                }
              },
              color =  MaterialTheme.colorScheme.onError,
              titleColor =  MaterialTheme.colorScheme.onError,
              modifier =  Modifier
                .padding(horizontal = 24.dp)
            )
          }
        }

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
          modifier = Modifier
            .padding(top = 48.dp)
        ) {
          Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
              .padding(24.dp)
          ) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                Icons.Outlined.People,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
              )

              Text(
                "Customer",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
              )
            }
            MainButtonView(
              title = stringResource(R.string.write_customer_tag),
              onClick = {
                if (doesDeviceSupportTagScanning) {
                  onShowItemTagWriteClick(viewModel.itemTagId, uiState.isLock, ItemTagType.Customer.param)
                } else {
                  viewModel.updateMessage(deviceDoesNotSupportTagScanningMessage)
                }
              },
              color =  MaterialTheme.colorScheme.onPrimary,
              titleColor =  MaterialTheme.colorScheme.onPrimary,
              modifier =  Modifier
                .padding(horizontal = 24.dp)
            )

            val captureController = rememberCaptureController()
            val scope = rememberCoroutineScope()

            Row(
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth(),
            ) {
              Button(
                onClick = {
                  // Capture content
                  scope.launch {
                    val bitmapAsync = captureController.captureAsync()
                    try {
                      val bitmap = bitmapAsync.await()
                      context.shareImage(
                        uiState.itemTag.getQueueNumber(),
                        bitmap,
                        uiState.itemTag.getQueueNumber()
                      )
                      // Do something with `bitmap`.
                    } catch (error: Throwable) {
                      // Error occurred, do something.
                      viewModel.updateMessage(error.localizedMessage ?: "")
                    }
                  }
                }
              ) {
                QrCode(viewModel, uiState, Modifier.capturable(captureController))
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun QrCode(
  viewModel: ItemTagDetailViewModel,
  uiState: ItemTagDetailUiState,
  modifier: Modifier,
) {
  val scanUri = Utility.scanUri(viewModel.itemTagId, ItemTagType.Customer.param)

  QrCodeView(
    data = scanUri.toString(),
    modifier
      .size(96.dp),
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .fillMaxSize()
        .clip(
          RectangleShape
        )
        .background(Color.White)
    ) {
      Text(
        uiState.itemTag.getQueueNumber(),
        color = Color.Black,
        fontSize = 7.sp.nonScaledSp,
        textAlign = TextAlign.Center,
      )
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
      contentAlignment = Alignment.Center
    ) {
      ErrorView(
        onClick = { viewModel.reload() }
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
      contentAlignment = Alignment.Center
    ) {
      LoadingView()
    }
  }
}
