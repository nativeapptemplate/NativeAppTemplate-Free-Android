package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.NatConstants
import com.nativeapptemplate.nativeapptemplatefree.data.login.SignUpRepository
import com.nativeapptemplate.nativeapptemplatefree.model.SendConfirmation
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility.validateEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResendConfirmationInstructionsUiState(
  val email: String = "",

  val isSent: Boolean = false,

  val isLoading: Boolean = false,
  val message: String = "",
)

@HiltViewModel
class ResendConfirmationInstructionsViewModel @Inject constructor(
  private val signUpRepository: SignUpRepository
) : ViewModel() {
  private val _uiState = MutableStateFlow(ResendConfirmationInstructionsUiState())
  val uiState: StateFlow<ResendConfirmationInstructionsUiState> = _uiState.asStateFlow()

  fun sendMeResetPasswordInstructions() {
    _uiState.update {
      it.copy(
        isLoading = true,
        isSent = false,
      )
    }

    viewModelScope.launch {
      val sendConfirmation = SendConfirmation(
        email = uiState.value.email.trim(),
        redirectUrl = SendConfirmation.redirectUrlString(NatConstants.baseUrlString())
      )

      val booleanFlow = signUpRepository.sendConfirmationInstruction(sendConfirmation)

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
              isSent = true,
            )
          }
        }
    }
  }

  fun hasInvalidData() : Boolean {
    return hasInvalidDataEmail()
  }

  fun hasInvalidDataEmail() : Boolean {
    if (uiState.value.email.isBlank()) return true

    return !uiState.value.email.validateEmail()
  }

  fun updateEmail(newEmail: String) {
    _uiState.update {
      it.copy(email = newEmail)
    }
  }

  fun snackbarMessageShown() {
    _uiState.update { it.copy(message = "") }
  }
}
