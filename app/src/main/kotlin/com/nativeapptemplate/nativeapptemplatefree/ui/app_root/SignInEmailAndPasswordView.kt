package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.nativeapptemplate.nativeapptemplatefree.ui.common.MainButtonView

@Composable
fun SignInEmailAndPasswordView(
  viewModel: SignInEmailAndPasswordViewModel = hiltViewModel(),
  onShowForgotPasswordClick: () -> Unit,
  onShowResendConfirmationInstructionsClick: () -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  SignInEmailAndPasswordView(
    viewModel,
    uiState,
    onShowForgotPasswordClick,
    onShowResendConfirmationInstructionsClick,
    onShowSnackbar,
    onBackClick
  )
}

@Composable
fun SignInEmailAndPasswordView(
  viewModel: SignInEmailAndPasswordViewModel,
  uiState: SignInEmailAndPasswordUiState,
  onShowForgotPasswordClick: () -> Unit,
  onShowResendConfirmationInstructionsClick: () -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  ContentView(
    viewModel,
    uiState,
    onShowForgotPasswordClick,
    onShowResendConfirmationInstructionsClick,
    onShowSnackbar,
    onBackClick
  )
}

@Composable
private fun ContentView(
  viewModel: SignInEmailAndPasswordViewModel,
  uiState: SignInEmailAndPasswordUiState,
  onShowForgotPasswordClick: () -> Unit,
  onShowResendConfirmationInstructionsClick: () -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  if (uiState.isLoading) {
    SignInEmailAndPasswordLoadingView(onBackClick)
  } else {
    SignInEmailAndPasswordContentView(
      viewModel,
      uiState,
      onShowForgotPasswordClick,
      onShowResendConfirmationInstructionsClick,
      onShowSnackbar,
      onBackClick
    )
  }
}

@Composable
fun SignInEmailAndPasswordContentView(
  viewModel: SignInEmailAndPasswordViewModel,
  uiState: SignInEmailAndPasswordUiState,
  onShowForgotPasswordClick: () -> Unit,
  onShowResendConfirmationInstructionsClick: () -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  var passwordVisible by rememberSaveable { mutableStateOf(false) }

  LaunchedEffect(uiState.message) {
    if (uiState.message.isNotBlank()) {
      onShowSnackbar(uiState.message, "dismiss", SnackbarDuration.Indefinite)
      viewModel.snackbarMessageShown()
    }
  }

  Scaffold(
    topBar = { TopAppBar(
      onBackClick = onBackClick
    ) },
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier
        .padding(padding)
        .padding(horizontal = 16.dp, vertical = 16.dp)
        .verticalScroll(rememberScrollState()),
    ) {
      OutlinedTextField(
        label = {
          Text(
            text = stringResource(R.string.email)
          )
        },
        placeholder = { Text(NatConstants.PLACEHOLDER_EMAIL) },
        value = uiState.email,
        onValueChange = { viewModel.updateEmail(it) },
        supportingText = {
          if (uiState.email.isBlank()) {
            Text(
              text = stringResource(id = R.string.email_is_required),
              style = MaterialTheme.typography.bodyLarge,
              color = Color.Red
            )
          } else if (viewModel.hasInvalidDataEmail()) {
            Text(
              text = stringResource(id = R.string.email_is_invalid),
              style = MaterialTheme.typography.bodyLarge,
              color = Color.Red
            )
          }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier
          .fillMaxWidth(),
      )

      OutlinedTextField(
        label = {
          Text(
            text = stringResource(R.string.label_password)
          )
        },
        placeholder = { Text(NatConstants.PLACEHOLDER_PASSWORD) },
        value = uiState.password,
        onValueChange = { viewModel.updatePassword(it) },
        supportingText = {
          if (uiState.password.isBlank()) {
            Text(
              text = stringResource(id = R.string.new_password_is_required),
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

      MainButtonView(
        title = stringResource(R.string.button_sign_in),
        onClick = { viewModel.login() },
        enabled = !viewModel.hasInvalidData(),
        modifier =  Modifier
          .padding(horizontal = 12.dp, vertical = 24.dp)
      )

      MainButtonView(
        title = stringResource(R.string.forgot_password),
        onClick = { onShowForgotPasswordClick() },
        modifier =  Modifier
          .padding(horizontal = 12.dp, vertical = 24.dp)
          .padding(top = 64.dp)
      )

      MainButtonView(
        title = stringResource(R.string.didnt_receive_confirmation_instructions),
        onClick = { onShowResendConfirmationInstructionsClick() },
        modifier =  Modifier
          .padding(horizontal = 12.dp, vertical = 24.dp)
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
    title = { Text(stringResource(R.string.sign_in)) },
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
private fun SignInEmailAndPasswordLoadingView(
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = { TopAppBar(onBackClick) },
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

