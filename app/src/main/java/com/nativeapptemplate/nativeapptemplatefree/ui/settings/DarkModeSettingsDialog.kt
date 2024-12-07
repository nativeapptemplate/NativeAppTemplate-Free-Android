@file:Suppress("ktlint:standard:max-line-length")

package com.nativeapptemplate.nativeapptemplatefree.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.NatTheme
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.model.DarkThemeConfig

@Composable
fun DarkModeSettingsDialog(
  onDismiss: () -> Unit,
  viewModel: DarkModeSettingsViewModel = hiltViewModel(),
) {
  val settingsUiState by viewModel.darkModeSettingsUiState.collectAsStateWithLifecycle()
  DarkModeSettingsDialog(
    onDismiss = onDismiss,
    settingsUiState = settingsUiState,
    onChangeDarkThemeConfig = viewModel::updateDarkThemeConfig,
  )
}

@Composable
fun DarkModeSettingsDialog(
  onDismiss: () -> Unit,
  settingsUiState: DarkModeSettingsUiState,
  onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
) {
  val configuration = LocalConfiguration.current

  /**
   * usePlatformDefaultWidth = false is use as a temporary fix to allow
   * height recalculation during recomposition. This, however, causes
   * Dialog's to occupy full width in Compact mode. Therefore max width
   * is configured below. This should be removed when there's fix to
   * https://issuetracker.google.com/issues/221643630
   */
  AlertDialog(
    properties = DialogProperties(usePlatformDefaultWidth = false),
    modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
    onDismissRequest = { onDismiss() },
    title = {
      Text(
        text = stringResource(R.string.label_dark_mode),
        style = MaterialTheme.typography.titleLarge,
      )
    },
    text = {
      HorizontalDivider()
      Column(Modifier.verticalScroll(rememberScrollState())) {
        when (settingsUiState) {
          DarkModeSettingsUiState.Loading -> {
            Text(
              text = stringResource(R.string.label_loading),
              modifier = Modifier.padding(vertical = 16.dp),
            )
          }

          is DarkModeSettingsUiState.Success -> {
            SettingsPanel(
              settings = settingsUiState.settings,
              onChangeDarkThemeConfig = onChangeDarkThemeConfig,
            )
          }
        }
      }
    },
    confirmButton = {
      Text(
        text = stringResource(R.string.button_label_ok),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
          .padding(horizontal = 8.dp)
          .clickable { onDismiss() },
      )
    },
  )
}

// [ColumnScope] is used for using the [ColumnScope.AnimatedVisibility] extension overload composable.
@Composable
private fun ColumnScope.SettingsPanel(
  settings: UserEditableSettings,
  onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
) {
  Column(Modifier.selectableGroup()) {
    SettingsDialogThemeChooserRow(
      text = stringResource(R.string.label_system_default),
      selected = settings.darkThemeConfig == DarkThemeConfig.FOLLOW_SYSTEM,
      onClick = { onChangeDarkThemeConfig(DarkThemeConfig.FOLLOW_SYSTEM) },
    )
    SettingsDialogThemeChooserRow(
      text = stringResource(R.string.label_light),
      selected = settings.darkThemeConfig == DarkThemeConfig.LIGHT,
      onClick = { onChangeDarkThemeConfig(DarkThemeConfig.LIGHT) },
    )
    SettingsDialogThemeChooserRow(
      text = stringResource(R.string.label_dark),
      selected = settings.darkThemeConfig == DarkThemeConfig.DARK,
      onClick = { onChangeDarkThemeConfig(DarkThemeConfig.DARK) },
    )
  }
}

@Composable
fun SettingsDialogThemeChooserRow(
  text: String,
  selected: Boolean,
  onClick: () -> Unit,
) {
  Row(
    Modifier
      .fillMaxWidth()
      .selectable(
        selected = selected,
        role = Role.RadioButton,
        onClick = onClick,
      )
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    RadioButton(
      selected = selected,
      onClick = null,
    )
    Spacer(Modifier.width(8.dp))
    Text(text)
  }
}

@Preview
@Composable
private fun PreviewSettingsDialog() {
  NatTheme {
    DarkModeSettingsDialog(
      onDismiss = {},
      settingsUiState = DarkModeSettingsUiState.Success(
        UserEditableSettings(
          darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
        ),
      ),
      onChangeDarkThemeConfig = {},
    )
  }
}

@Preview
@Composable
private fun PreviewSettingsDialogLoading() {
  NatTheme {
    DarkModeSettingsDialog(
      onDismiss = {},
      settingsUiState = DarkModeSettingsUiState.Loading,
      onChangeDarkThemeConfig = {},
    )
  }
}
