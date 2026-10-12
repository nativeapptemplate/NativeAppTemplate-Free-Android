package com.nativeapptemplate.nativeapptemplatefree.ui.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType.Companion.PrimaryEditable
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nativeapptemplate.nativeapptemplatefree.NativeAppTemplateConstants
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.model.Locales
import com.nativeapptemplate.nativeapptemplatefree.model.TimeZones
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ErrorView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadOnceEffect
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.MainButtonView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.SnackbarMessageEffect
import com.nativeapptemplate.nativeapptemplatefree.ui.common.SubmitFab
import com.nativeapptemplate.nativeapptemplatefree.utils.AppLanguage

@Composable
fun ShopkeeperEditView(
  viewModel: ShopkeeperEditViewModel = hiltViewModel(),
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val dismissLabel = stringResource(R.string.dismiss)
  val shopkeeperUpdatedMessage = stringResource(R.string.message_shopkeeper_updated)

  LoadOnceEffect(viewModel::reload)

  SnackbarMessageEffect(
    message = uiState.message,
    onShowSnackbar = onShowSnackbar,
    onMessageShown = viewModel::snackbarMessageShown,
  )

  LaunchedEffect(uiState.isUpdated) {
    if (uiState.isUpdated) {
      onShowSnackbar(shopkeeperUpdatedMessage, dismissLabel, SnackbarDuration.Short)
      viewModel.reload()
    }
  }

  ShopkeeperEditView(
    viewModel,
    uiState,
    onBackClick,
  )
}

@Composable
fun ShopkeeperEditView(
  viewModel: ShopkeeperEditViewModel,
  uiState: ShopkeeperEditUiState,
  onBackClick: () -> Unit,
) {
  ContentView(viewModel, uiState, onBackClick)
}

@Composable
private fun ContentView(
  viewModel: ShopkeeperEditViewModel,
  uiState: ShopkeeperEditUiState,
  onBackClick: () -> Unit,
) {
  if (uiState.isLoading) {
    ShopkeeperEditLoadingView(onBackClick)
  } else if (uiState.success) {
    ShopkeeperEditContentView(viewModel, uiState, onBackClick)
  } else {
    ShopkeeperEditErrorView(viewModel, onBackClick)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopkeeperEditContentView(
  viewModel: ShopkeeperEditViewModel,
  uiState: ShopkeeperEditUiState,
  onBackClick: () -> Unit,
) {
  val timeZoneValues = TimeZones.map.values.toList()
  var timeZoneDropdownMenuExpanded by remember { mutableStateOf(false) }
  var isShowingDeleteConfirmationDialog by remember { mutableStateOf(false) }

  if (isShowingDeleteConfirmationDialog) {
    DeleteShopkeeperAlertDialog(
      dialogTitle = stringResource(R.string.are_you_sure),
      confirmButtonTitle = stringResource(R.string.delete_my_account),
      onDismissRequest = { isShowingDeleteConfirmationDialog = false },
      onConfirmation = { viewModel.deleteShopkeeper() },
      icon = Icons.Outlined.AddAlert,
    )
  }

  Scaffold(
    topBar = {
      TopAppBar(onBackClick)
    },
    floatingActionButton = {
      SubmitFab(
        label = stringResource(R.string.button_label_update),
        onClick = { viewModel.updateShopkeeper() },
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
            text = stringResource(R.string.full_name),
          )
        },
        placeholder = { Text(NativeAppTemplateConstants.PLACEHOLDER_FULLNAME) },
        value = uiState.name,
        onValueChange = { viewModel.updateName(it) },
        supportingText = {
          if (uiState.name.isBlank()) {
            Text(
              text = stringResource(id = R.string.full_name_is_required),
              style = MaterialTheme.typography.bodyLarge,
              color = Color.Red,
            )
          }
        },
        modifier = Modifier
          .fillMaxWidth(),
      )

      OutlinedTextField(
        label = {
          Text(
            text = stringResource(R.string.email),
          )
        },
        placeholder = { Text(NativeAppTemplateConstants.PLACEHOLDER_EMAIL) },
        value = uiState.email,
        onValueChange = { viewModel.updateEmail(it) },
        supportingText = {
          if (uiState.email.isBlank()) {
            Text(
              text = stringResource(id = R.string.email_is_required),
              style = MaterialTheme.typography.bodyLarge,
              color = Color.Red,
            )
          } else if (viewModel.hasInvalidDataEmail()) {
            Text(
              text = stringResource(id = R.string.email_is_invalid),
              style = MaterialTheme.typography.bodyLarge,
              color = Color.Red,
            )
          }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier
          .fillMaxWidth(),
      )

      ExposedDropdownMenuBox(
        expanded = timeZoneDropdownMenuExpanded,
        onExpandedChange = { timeZoneDropdownMenuExpanded = it },
      ) {
        TextField(
          // The `menuAnchor` modifier must be passed to the text field for correctness.
          modifier = Modifier.menuAnchor(PrimaryEditable, true),
          readOnly = true,
          value = TimeZones.displayName(uiState.timeZone),
          onValueChange = {},
          label = { Text(stringResource(R.string.time_zone)) },
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeZoneDropdownMenuExpanded) },
          colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
          expanded = timeZoneDropdownMenuExpanded,
          onDismissRequest = { timeZoneDropdownMenuExpanded = false },
        ) {
          timeZoneValues.forEach { selectionTimeZoneValue ->
            DropdownMenuItem(
              text = { Text(selectionTimeZoneValue) },
              onClick = {
                viewModel.updateTimeZone(TimeZones.keyFromValue(selectionTimeZoneValue))
                timeZoneDropdownMenuExpanded = false
              },
              contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
            )
          }
        }
      }

      AppLanguageSection()

      MainButtonView(
        title = stringResource(R.string.delete_my_account),
        onClick = { isShowingDeleteConfirmationDialog = true },
        modifier = Modifier
          .padding(horizontal = 12.dp, vertical = 24.dp),
      )
    }
  }
}

/**
 * The app's language, read-only: it is changed in the system settings (per-app language on
 * Android 13+, the device language before), and AppLanguageSyncer makes the server follow it.
 */
@Composable
private fun AppLanguageSection() {
  val context = LocalContext.current
  // Changing the language recreates the Activity, so this is read again on return from Settings.
  val appLanguage = AppLanguage.currentCode(context)

  Column(
    verticalArrangement = Arrangement.spacedBy(4.dp),
    modifier = Modifier.fillMaxWidth(),
  ) {
    Text(
      text = stringResource(R.string.app_language),
      style = MaterialTheme.typography.titleMedium,
    )
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
        text = languageDisplayName(appLanguage),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.weight(1f),
      )
      TextButton(
        onClick = { openAppLanguageSettings(context) },
      ) {
        Text(stringResource(R.string.change_app_language_in_settings))
      }
    }
    Text(
      text = stringResource(R.string.app_language_emails_too),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

private fun openAppLanguageSettings(context: Context) {
  try {
    context.startActivity(AppLanguage.settingsIntent(context.packageName))
  } catch (exception: ActivityNotFoundException) {
    // A device whose Settings app lacks the per-app screen: the device language still applies.
    Log.w("ShopkeeperEditView", "No app language settings screen", exception)
    try {
      context.startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    } catch (fallbackException: ActivityNotFoundException) {
      Log.w("ShopkeeperEditView", "No language settings screen", fallbackException)
    }
  }
}

/** Each language is shown in its own name, so it can be found whatever the current language is. */
@Composable
private fun languageDisplayName(locale: String): String = when (locale) {
  Locales.JAPANESE -> stringResource(R.string.language_japanese)
  Locales.ENGLISH -> stringResource(R.string.language_english)
  else -> locale
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
    title = { Text(stringResource(R.string.edit_profile)) },
    navigationIcon = {
      IconButton(onClick = {
        onBackClick()
      }) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
      }
    },
    modifier = Modifier.fillMaxWidth(),
  )
}

@Composable
fun DeleteShopkeeperAlertDialog(
  dialogTitle: String,
  confirmButtonTitle: String,
  onDismissRequest: (() -> Unit),
  onConfirmation: (() -> Unit),
  icon: ImageVector,
) {
  AlertDialog(
    icon = {
      Icon(
        icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
          .size(48.dp),
      )
    },
    title = {
      Text(dialogTitle)
    },
    onDismissRequest = {
      onDismissRequest()
    },
    confirmButton = {
      TextButton(
        onClick = {
          onConfirmation()
        },
      ) {
        Text(
          confirmButtonTitle,
          color = Color.Red,
        )
      }
    },
    dismissButton = {
      TextButton(
        onClick = {
          onDismissRequest()
        },
      ) {
        Text(stringResource(R.string.dismiss))
      }
    },
  )
}

@Composable
private fun ShopkeeperEditErrorView(
  viewModel: ShopkeeperEditViewModel,
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
      contentAlignment = Alignment.Center,
    ) {
      ErrorView(
        onClick = { viewModel.reload() },
      )
    }
  }
}

@Composable
private fun ShopkeeperEditLoadingView(
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
