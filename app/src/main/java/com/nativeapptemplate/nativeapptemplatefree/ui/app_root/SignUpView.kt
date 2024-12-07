package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType.Companion.PrimaryEditable
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.nativeapptemplate.nativeapptemplatefree.model.TimeZones
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NatAlertDialog

@Composable
fun SignUpView(
  viewModel: SignUpViewModel = hiltViewModel(),
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(uiState.message) {
    if (uiState.message.isNotBlank()) {
      onShowSnackbar(uiState.message, "dismiss", SnackbarDuration.Indefinite)
      viewModel.snackbarMessageShown()
    }
  }

  if (uiState.isCreated) {
    NatAlertDialog(
      dialogTitle= stringResource(R.string.signed_up_but_unconfirmed),
      onDismissRequest = { onBackClick() },
    )
  }

  SignUpView(
    viewModel,
    uiState,
    onBackClick
  )
}

@Composable
fun SignUpView(
  viewModel: SignUpViewModel,
  uiState: SignUpUiState,
  onBackClick: () -> Unit,
) {
  ContentView(viewModel, uiState, onBackClick)
}

@Composable
private fun ContentView(
  viewModel: SignUpViewModel,
  uiState: SignUpUiState,
  onBackClick: () -> Unit,
) {
  if (uiState.isLoading) {
    SignUpLoadingView(onBackClick)
  } else {
    SignUpContentView(viewModel, uiState, onBackClick)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpContentView(
  viewModel: SignUpViewModel,
  uiState: SignUpUiState,
  onBackClick: () -> Unit,
) {
  val timeZoneValues = TimeZones.map.values.toList()
  var timeZoneDropdownMenuExpanded by remember { mutableStateOf(false) }
  var passwordVisible by rememberSaveable { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(onBackClick)
    },
    floatingActionButton = {
      // FloatingActionButton doesn't support the enabled property
      // https://stackoverflow.com/a/68853697/1160200
      Button(
        onClick = { viewModel.createShopkeeper() },
        modifier = Modifier.defaultMinSize(minWidth = 64.dp, minHeight = 64.dp),
        enabled = !viewModel.hasInvalidData(),
        shape = CircleShape

      ){
        Icon(Icons.Filled.Done, contentDescription = null)
        Text(
          stringResource(R.string.sign_up),
          modifier = Modifier
            .padding(start = 12.dp)
        )
      }
    },
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Column(
      modifier = Modifier
        .padding(padding)
        .padding(horizontal = 16.dp, vertical = 16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
      OutlinedTextField(
        label = {
          Text(
            text = stringResource(R.string.full_name)
          )
        },
        placeholder = { Text(NatConstants.PLACEHOLDER_FULLNAME) },
        value = uiState.name,
        onValueChange = { viewModel.updateName(it) },
        supportingText = {
          Text(
            text = stringResource(id = R.string.full_name_is_required),
            style = MaterialTheme.typography.bodyLarge,
            color = if (uiState.name.isBlank()) Color.Red else Color.Transparent
          )
        },
        modifier = Modifier
          .fillMaxWidth(),
      )

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

      ExposedDropdownMenuBox(
        expanded = timeZoneDropdownMenuExpanded,
        onExpandedChange = { timeZoneDropdownMenuExpanded = it },
      ) {
        TextField(
          // The `menuAnchor` modifier must be passed to the text field for correctness.
          modifier = Modifier.menuAnchor(PrimaryEditable, true),
          readOnly = true,
          value = TimeZones.map[uiState.timeZone]!!,
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

      OutlinedTextField(
        label = {
          Text(
            text = stringResource(R.string.password)
          )
        },
        placeholder = { Text(NatConstants.PLACEHOLDER_PASSWORD) },
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
    title = { Text(stringResource(R.string.sign_up)) },
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
private fun SignUpLoadingView(
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
