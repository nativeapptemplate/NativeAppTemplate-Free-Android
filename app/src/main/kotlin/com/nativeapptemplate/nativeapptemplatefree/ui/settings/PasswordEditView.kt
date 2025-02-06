package com.nativeapptemplate.nativeapptemplatefree.ui.settings

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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nativeapptemplate.nativeapptemplatefree.NatConstants
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView

@Composable
fun PasswordEditView(
  viewModel: PasswordEditViewModel = hiltViewModel(),
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val passwordUpdatedMessage = stringResource(R.string.message_password_updated)

  LaunchedEffect(uiState.message) {
    if (uiState.message.isNotBlank()) {
      onShowSnackbar(uiState.message, "dismiss", SnackbarDuration.Indefinite)
      viewModel.snackbarMessageShown()
    }
  }

  LaunchedEffect(uiState.isUpdated) {
    if (uiState.isUpdated) {
      onShowSnackbar(passwordUpdatedMessage, "dismiss", SnackbarDuration.Short)
    }
  }

  PasswordEditView(
    viewModel,
    uiState,
    onBackClick
  )
}

@Composable
fun PasswordEditView(
  viewModel: PasswordEditViewModel,
  uiState: PasswordEditUiState,
  onBackClick: () -> Unit,
) {
  ContentView(viewModel, uiState, onBackClick)
}

@Composable
private fun ContentView(
  viewModel: PasswordEditViewModel,
  uiState: PasswordEditUiState,
  onBackClick: () -> Unit,
) {
  if (uiState.isLoading) {
    PasswordEditLoadingView(onBackClick)
  } else {
    PasswordEditContentView(viewModel, uiState, onBackClick)
  }
}

@Composable
fun PasswordEditContentView(
  viewModel: PasswordEditViewModel,
  uiState: PasswordEditUiState,
  onBackClick: () -> Unit,
) {
  var currentPasswordVisible by rememberSaveable { mutableStateOf(false) }
  var passwordVisible by rememberSaveable { mutableStateOf(false) }
  var passwordConfirmationVisible by rememberSaveable { mutableStateOf(false) }


  Scaffold(
    topBar = {
      TopAppBar(onBackClick)
    },
    floatingActionButton = {
      // FloatingActionButton doesn't support the enabled property
      // https://stackoverflow.com/a/68853697/1160200
      Button(
        onClick = { viewModel.updatePassword() },
        modifier = Modifier.defaultMinSize(minWidth = 64.dp, minHeight = 64.dp),
        enabled = !viewModel.hasInvalidData(),
        shape = CircleShape

      ){
        Icon(Icons.Filled.Done, contentDescription = null)
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
            text = stringResource(R.string.current_password)
          )
        },
        placeholder = { Text(stringResource(R.string.current_password)) },
        value = uiState.currentPassword,
        onValueChange = { viewModel.updateCurrentPassword(it) },
        supportingText = {
          Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Text(
              text = stringResource(id = R.string.we_need_your_current_password),
              style = MaterialTheme.typography.bodyLarge,
            )
            Text(
              text = stringResource(id = R.string.current_password_is_required),
              style = MaterialTheme.typography.bodyLarge,
              color = if (uiState.currentPassword.isBlank()) Color.Red else Color.Transparent
            )
          }
        },
        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
          val image = if (currentPasswordVisible)
            Icons.Filled.Visibility
          else Icons.Filled.VisibilityOff

          val description = if (currentPasswordVisible) "Hide password" else "Show password"

          IconButton(onClick = {currentPasswordVisible = !currentPasswordVisible}){
            Icon(imageVector  = image, description)
          }
        },
        modifier = Modifier
          .fillMaxWidth(),
      )

      OutlinedTextField(
        label = {
          Text(
            text = stringResource(R.string.new_password)
          )
        },
        placeholder = { Text(stringResource(R.string.new_password)) },
        value = uiState.password,
        onValueChange = { viewModel.updatePassword(it) },
        supportingText = {
          Column {
            Text(
              text = "${NatConstants.MINIMUM_PASSWORD_LENGTH} characters minimum.",
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (uiState.password.isBlank()) {
              Text(
                text = stringResource(id = R.string.password_is_required),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Red
              )
            } else if (viewModel.hasInvalidDataPassword()) {
              Text(
                text = stringResource(id = R.string.password_is_invalid),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Red
              )
            }
          }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
          val image = if (passwordVisible)
            Icons.Filled.Visibility
          else Icons.Filled.VisibilityOff

          val description = if (passwordVisible) "Hide password" else "Show password"

          IconButton(onClick = {passwordVisible = !passwordVisible}){
            Icon(imageVector  = image, description)
          }
        },
        modifier = Modifier
          .fillMaxWidth(),
      )

      OutlinedTextField(
        label = {
          Text(
            text = stringResource(R.string.confirm_new_password)
          )
        },
        placeholder = { Text(stringResource(R.string.confirm_new_password)) },
        value = uiState.passwordConfirmation,
        onValueChange = { viewModel.updatePasswordConfirmation(it) },
        supportingText = {
          Text(
            text = stringResource(id = R.string.confirm_new_password_is_required),
            style = MaterialTheme.typography.bodyLarge,
            color = if (uiState.passwordConfirmation.isBlank()) Color.Red else Color.Transparent
          )
        },
        visualTransformation = if (passwordConfirmationVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
          val image = if (passwordConfirmationVisible)
            Icons.Filled.Visibility
          else Icons.Filled.VisibilityOff

          val description = if (passwordConfirmationVisible) "Hide password" else "Show password"

          IconButton(onClick = {passwordConfirmationVisible = !passwordConfirmationVisible}){
            Icon(imageVector  = image, description)
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
    title = { Text(stringResource(R.string.update_password)) },
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
private fun PasswordEditLoadingView(
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
