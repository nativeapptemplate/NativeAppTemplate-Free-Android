package com.nativeapptemplate.nativeapptemplatefree.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.NatConstants
import com.nativeapptemplate.nativeapptemplatefree.data.login.AccountPasswordRepository
import com.nativeapptemplate.nativeapptemplatefree.model.UpdatePasswordBody
import com.nativeapptemplate.nativeapptemplatefree.model.UpdatePasswordBodyDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PasswordEditUiState(
  val currentPassword: String = "",
  val password: String = "",
  val passwordConfirmation: String = "",

  val isUpdated: Boolean = false,

  val isLoading: Boolean = false,
  val message: String = "",
)

@HiltViewModel
class PasswordEditViewModel @Inject constructor(
  private val accountPasswordRepository: AccountPasswordRepository
) : ViewModel() {
  private val _uiState = MutableStateFlow(PasswordEditUiState())
  val uiState: StateFlow<PasswordEditUiState> = _uiState.asStateFlow()

  fun updatePassword() {
    _uiState.update {
      it.copy(
        isLoading = true,
        isUpdated = false,
      )
    }

    viewModelScope.launch {
      val updatePasswordBodyDetail = UpdatePasswordBodyDetail(
        currentPassword = uiState.value.currentPassword.trim(),
        password = uiState.value.password.trim(),
        passwordConfirmation = uiState.value.passwordConfirmation.trim(),
      )
      val updatePasswordBody = UpdatePasswordBody(updatePasswordBodyDetail)

      val booleanFlow = accountPasswordRepository.updateAccountPassword(updatePasswordBody)

      booleanFlow
        .catch { exception ->
          val message = exception.message
          _uiState.update {
            it.copy(
              message = message ?: "Unknown Error",
              isLoading = false,
            )
          }
        }
        .collect {
          _uiState.update {
            it.copy(
              isUpdated = true,
              isLoading = false,
            )
          }
        }
    }
  }

  fun hasInvalidData() : Boolean {
    if (
      uiState.value.currentPassword.isBlank() ||
      uiState.value.password.isBlank() ||
      uiState.value.passwordConfirmation.isBlank()
    ) {
      return true
    }

    if (hasInvalidDataPassword()) return true

    return false
  }

  fun hasInvalidDataPassword() : Boolean {
    if (uiState.value.password.isBlank()) return true
    if (uiState.value.password.length < NatConstants.MINIMUM_PASSWORD_LENGTH) return true

    return false
  }

  fun updatePassword(newPassword: String) {
    _uiState.update {
      it.copy(password = newPassword)
    }
  }

  fun updateCurrentPassword(newCurrentPassword: String) {
    _uiState.update {
      it.copy(currentPassword = newCurrentPassword)
    }
  }

  fun updatePasswordConfirmation(newPasswordConfirmation: String) {
    _uiState.update {
      it.copy(passwordConfirmation = newPasswordConfirmation)
    }
  }

  fun snackbarMessageShown() {
    _uiState.update { it.copy(message = "") }
  }
}
