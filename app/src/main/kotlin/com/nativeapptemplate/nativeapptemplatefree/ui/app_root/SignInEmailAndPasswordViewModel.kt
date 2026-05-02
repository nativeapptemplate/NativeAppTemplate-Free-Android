package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.NativeAppTemplateConstants
import com.nativeapptemplate.nativeapptemplatefree.common.errors.codedDescription
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Login
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignInEmailAndPasswordUiState(
  val email: String = "",
  val password: String = "",

  val loggedInShopkeeper: LoggedInShopkeeper = LoggedInShopkeeper(),

  val isLoading: Boolean = false,
  val message: String = "",
)

@HiltViewModel
class SignInEmailAndPasswordViewModel @Inject constructor(
  private val loginRepository: LoginRepository,
) : ViewModel() {
  private val _uiState = MutableStateFlow(SignInEmailAndPasswordUiState())
  val uiState: StateFlow<SignInEmailAndPasswordUiState> = _uiState.asStateFlow()

  fun login() {
    _uiState.update {
      it.copy(
        isLoading = true,
      )
    }

    viewModelScope.launch {
      val login = Login(uiState.value.email, uiState.value.password)
      val loggedInShopkeeperFlow: Flow<LoggedInShopkeeper> = loginRepository.login(login)

      loggedInShopkeeperFlow
        .catch { exception ->
          val message = exception.codedDescription
          _uiState.update {
            it.copy(
              message = message,
              isLoading = false,
            )
          }
        }
        .collect { loggedInShopkeeper ->
          _uiState.update { it.copy(loggedInShopkeeper = loggedInShopkeeper) }
          loginRepository.clearUserPreferences()
          loginRepository.setShopkeeper(loggedInShopkeeper)

          val permissionsFlow: Flow<Permissions> = loginRepository.getPermissions()

          permissionsFlow
            .catch { exception ->
              Log.e("SignInEmailAndPasswordViewModel", "Failed to update permissions", exception)
              val booleanFlow = loginRepository.logout()
              booleanFlow
                .catch { logoutException ->
                  Log.e("SignInEmailAndPasswordViewModel", "Logout error", logoutException)
                }
                .collect {
                }
            }
            .collect { permissions ->
              loginRepository.setPermissions(permissions)
              _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
  }

  fun hasInvalidData(): Boolean {
    if (hasInvalidDataEmail()) return true
    if (hasInvalidDataPassword()) return true

    return false
  }

  fun hasInvalidDataEmail(): Boolean {
    if (uiState.value.email.isBlank()) return true

    return !uiState.value.email.isValidEmail()
  }

  fun hasInvalidDataPassword(): Boolean {
    if (uiState.value.password.isBlank()) return true
    if (uiState.value.password.length < NativeAppTemplateConstants.MINIMUM_PASSWORD_LENGTH) return true

    return false
  }

  fun updateEmail(newEmail: String) {
    _uiState.update {
      it.copy(email = newEmail)
    }
  }

  fun updatePassword(newPassword: String) {
    _uiState.update {
      it.copy(password = newPassword)
    }
  }

  fun snackbarMessageShown() {
    _uiState.update { it.copy(message = "") }
  }
}
