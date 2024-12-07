package com.nativeapptemplate.nativeapptemplatefree.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nativeapptemplate.nativeapptemplatefree.BuildConfig
import com.nativeapptemplate.nativeapptemplatefree.NatConstants
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.model.DarkThemeConfig
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ErrorView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.MainButtonView
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility

@Composable
internal fun SettingsView(
  viewModel: SettingsViewModel = hiltViewModel(),
  onShowShopkeeperEditClick: () -> Unit,
  onShowPasswordEditClick: () -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
) {
  val uiState: SettingsUiState by viewModel.uiState.collectAsStateWithLifecycle()

  LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    viewModel.reload()
  }

  LaunchedEffect(uiState.message) {
    if (uiState.message.isNotBlank()) {
      onShowSnackbar(uiState.message, "dismiss", SnackbarDuration.Indefinite)
      viewModel.snackbarMessageShown()
    }
  }

  SettingsView(
    viewModel = viewModel,
    uiState = uiState,
    onShowShopkeeperEditClick = onShowShopkeeperEditClick,
    onShowPasswordEditClick = onShowPasswordEditClick,
  )
}

@Composable
fun SettingsView(
  viewModel: SettingsViewModel,
  uiState: SettingsUiState,
  onShowShopkeeperEditClick: () -> Unit,
  onShowPasswordEditClick: () -> Unit,
) {
  ContentView(
    viewModel = viewModel,
    uiState = uiState,
    onShowShopkeeperEditClick = onShowShopkeeperEditClick,
    onShowPasswordEditClick = onShowPasswordEditClick,
  )
}

@Composable
private fun ContentView(
  viewModel: SettingsViewModel,
  uiState: SettingsUiState,
  onShowShopkeeperEditClick: () -> Unit,
  onShowPasswordEditClick: () -> Unit,
) {
  if (uiState.isLoading) {
    SettingsLoadingView()
  } else if (uiState.success) {
    SettingsContentView(
      viewModel = viewModel,
      uiState = uiState,
      onShowShopkeeperEditClick = onShowShopkeeperEditClick,
      onShowPasswordEditClick = onShowPasswordEditClick,
    )
  } else {
    SettingsErrorView(viewModel)
  }
}

@Composable
private fun SettingsContentView(
  viewModel: SettingsViewModel,
  uiState: SettingsUiState,
  onShowShopkeeperEditClick: () -> Unit,
  onShowPasswordEditClick: () -> Unit,
) {
  var isShowingDarkModeSettingsDialog by rememberSaveable { mutableStateOf(false) }
  val userData = uiState.userData
  val darkModeResId = when (userData.darkThemeConfig) {
    DarkThemeConfig.DARK -> R.string.label_dark
    DarkThemeConfig.LIGHT -> R.string.label_light
    else -> R.string.label_system_default
  }
  val context = LocalContext.current

  if (isShowingDarkModeSettingsDialog) {
    DarkModeSettingsDialog(
      onDismiss = { isShowingDarkModeSettingsDialog = false },
    )
  }

  Scaffold(
    topBar = { TopAppBar() },
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
          ListItem(
            headlineContent = {
              Text(
                stringResource(R.string.my_account),
                style = MaterialTheme.typography.titleMedium,
              )
            },
          )
          HorizontalDivider()
        }
        item {
          ListItem(
            headlineContent = {
              Text(
                stringResource(R.string.profile),
                style = MaterialTheme.typography.titleMedium
              )
            },
            leadingContent = {
              Icon(
                Icons.Outlined.Person,
                contentDescription = stringResource(R.string.profile),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            },
            modifier = Modifier
              .clickable {
                onShowShopkeeperEditClick()
              },
          )
          HorizontalDivider()
        }

        item {
          ListItem(
            headlineContent = {
              Text(
                stringResource(R.string.label_password),
                style = MaterialTheme.typography.titleMedium
              )
            },
            leadingContent = {
              Icon(
                Icons.Outlined.Key,
                contentDescription = stringResource(R.string.label_password),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            },
            modifier = Modifier
              .clickable {
                onShowPasswordEditClick()
              },
          )
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
          ListItem(
            headlineContent = {
              Text(
                stringResource(R.string.dark_mode_preference),
                style = MaterialTheme.typography.titleMedium,
              )
            },
          )
          HorizontalDivider()
        }
        item {
          ListItem(
            headlineContent = { Text(stringResource(R.string.mode)) },
            trailingContent = { Text(stringResource(darkModeResId)) },
            modifier = Modifier
              .clickable { isShowingDarkModeSettingsDialog = true },
          )
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
          ListItem(
            headlineContent = {
              Text(
                stringResource(R.string.information),
                style = MaterialTheme.typography.titleMedium
              )
            },
          )
          HorizontalDivider()
        }
        item {
          ListItem(
            headlineContent = {
              Text(
                stringResource(R.string.support_website),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
              )
            },
            leadingContent = {
              Icon(
                Icons.Outlined.Info,
                contentDescription = stringResource(R.string.support_website),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            },
            modifier = Modifier
              .clickable {
                context.startActivity(
                  Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(NatConstants.SUPPORT_WEBSITE_URL)
                  )
                )
              },
          )
          HorizontalDivider()
        }
        item {
          ListItem(
            headlineContent = {
              Text(
                stringResource(R.string.discussions),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
              )
            },
            leadingContent = {
              Icon(
                Icons.Outlined.Forum,
                contentDescription = stringResource(R.string.discussions),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            },
            modifier = Modifier
              .clickable {
                context.startActivity(
                  Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(NatConstants.DISCUSSIONS_URL)
                  )
                )
              },
          )
          HorizontalDivider()
        }
        item {
          val emailAppNotFoundMessage = stringResource(R.string.email_app_is_not_found)

          ListItem(
            headlineContent = {
              Text(
                stringResource(R.string.contact),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
              )
            },
            leadingContent = {
              Icon(
                Icons.Outlined.Mail,
                contentDescription = stringResource(R.string.contact),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            },
            modifier = Modifier
              .clickable {
                try {
                  Utility.sendContactMail(context)
                } catch (exception: ActivityNotFoundException) {
                  viewModel.updateMessage(emailAppNotFoundMessage)
                }
              },
          )
          HorizontalDivider()
        }
        item {
          ListItem(
            headlineContent = {
              Text(
                stringResource(R.string.rate_app),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
              )
            },
            leadingContent = {
              Icon(
                Icons.Outlined.ThumbUp,
                contentDescription = stringResource(R.string.rate_app),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            },
            modifier = Modifier
              .clickable {
                try {
                  context.startActivity(Intent(Intent.ACTION_VIEW, Utility.marketUri()))
                } catch (e: ActivityNotFoundException) {
                  context.startActivity(Intent(Intent.ACTION_VIEW, Utility.googlePlayStoreUri()))
                }
              },
          )
          HorizontalDivider()
        }
        item {
          ListItem(
            headlineContent = {
              Text(
                stringResource(R.string.privacy_policy),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
              )
            },
            modifier = Modifier
              .clickable {
                context.startActivity(
                  Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(NatConstants.PRIVACY_POLICY_URL)
                  )
                )
              },
          )
          HorizontalDivider()
        }
        item {
          ListItem(
            headlineContent = {
              Text(
                stringResource(R.string.terms_of_use),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
              )
            },
            modifier = Modifier
              .clickable {
                context.startActivity(
                  Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(NatConstants.TERMS_OF_USE_URL)
                  )
                )
              },
          )
          HorizontalDivider()
        }

        item {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .padding(top = 48.dp)
          ) {
            Text("Logged in as ${userData.name}")

            MainButtonView(
              title = stringResource(R.string.button_sign_out),
              onClick = { viewModel.logout() },
              modifier = Modifier
                .padding(top = 12.dp)
            )
          }
        }
      }
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
    title = { Text(text = stringResource(id = R.string.title_settings)) },
    modifier = Modifier.fillMaxWidth(),
  )
}

@Composable
private fun SettingsErrorView(
  viewModel: SettingsViewModel,
) {
  Scaffold(
    topBar = {
      TopAppBar(
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
private fun SettingsLoadingView(
) {
  Scaffold(
    topBar = { TopAppBar() },
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



