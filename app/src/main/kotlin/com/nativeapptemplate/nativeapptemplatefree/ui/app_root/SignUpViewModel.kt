package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.NatConstants
import com.nativeapptemplate.nativeapptemplatefree.data.login.SignUpRepository
import com.nativeapptemplate.nativeapptemplatefree.model.SignUp
import com.nativeapptemplate.nativeapptemplatefree.model.TimeZones
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility.validateEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignUpUiState(
  val name: String = "",
  val email: String = "",
  val password: String = "",
  val timeZone: String = TimeZones.currentTimeZoneKey(),

  val isCreated: Boolean = false,

  val isLoading: Boolean = false,
  val message: String = "",
)

@HiltViewModel
class SignUpViewModel @Inject constructor(
  private val signUpRepository: SignUpRepository
) : ViewModel() {
  private val _uiState = MutableStateFlow(SignUpUiState())
  val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

  fun createShopkeeper() {
    _uiState.update {
      it.copy(
        isLoading = true,
        isCreated = false,
      )
    }

    viewModelScope.launch {
      val signUp = SignUp(
        name = uiState.value.name,
        email = uiState.value.email.trim(),
        password = uiState.value.password.trim(),
        timeZone = uiState.value.timeZone,
        currentPlatform = "android"
      )

      val loggedInShopkeeperFlow = signUpRepository.signUp(signUp)

      loggedInShopkeeperFlow
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
              isCreated = true,
            )
          }
        }
    }
  }

  fun hasInvalidData() : Boolean {
    if (uiState.value.name.isBlank()) return true

    if (hasInvalidDataEmail()) return true

    if (hasInvalidDataPassword()) return true

    return false
  }

  fun hasInvalidDataEmail() : Boolean {
    if (uiState.value.email.isBlank()) return true

    return !uiState.value.email.validateEmail()
  }

  fun hasInvalidDataPassword() : Boolean {
    if (uiState.value.password.isBlank()) return true
    if (uiState.value.password.length < NatConstants.MINIMUM_PASSWORD_LENGTH) return true

    return false
  }

  fun updateName(newName: String) {
    _uiState.update {
      it.copy(name = newName)
    }
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

  fun updateTimeZone(newTimeZone: String) {
    _uiState.update {
      it.copy(timeZone = newTimeZone)
    }
  }

  fun snackbarMessageShown() {
    _uiState.update { it.copy(message = "") }
  }
}
