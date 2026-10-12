package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ErrorShowingUiState
import com.nativeapptemplate.nativeapptemplatefree.ui.common.catchShowingError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AcceptTermsUiState(
  val email: String = "",

  val isUpdated: Boolean = false,

  val isLoading: Boolean = false,
  val message: String = "",
) : ErrorShowingUiState<AcceptTermsUiState> {
  override fun showingError(message: String) = copy(message = message, isLoading = false)
}

@HiltViewModel
class AcceptTermsViewModel @Inject constructor(
  private val loginRepository: LoginRepository,
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
        .catchShowingError(_uiState)
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
