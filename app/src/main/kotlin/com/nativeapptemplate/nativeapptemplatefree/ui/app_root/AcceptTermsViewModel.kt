package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AcceptTermsUiState(
  val email: String = "",

  val isUpdated: Boolean = false,

  val isLoading: Boolean = false,
  val message: String = "",
)

@HiltViewModel
class AcceptTermsViewModel @Inject constructor(
  private val loginRepository: LoginRepository
) : ViewModel() {
  private val _uiState = MutableStateFlow(AcceptTermsUiState())
  val uiState: StateFlow<AcceptTermsUiState> = _uiState.asStateFlow()

  fun updateConfirmedTermsVersion() {
    _uiState.update {
      it.copy(
        isLoading = true,
        isUpdated = false,
      )
    }

    viewModelScope.launch {
      val booleanFlow = loginRepository.updateConfirmedTermsVersion()

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
            )
          }
        }
    }
  }

  fun snackbarMessageShown() {
    _uiState.update { it.copy(message = "") }
  }
}
