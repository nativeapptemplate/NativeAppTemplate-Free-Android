package com.nativeapptemplate.nativeapptemplatefree.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.BuildConfig
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
  val userData: UserData = UserData(),
  val isLoading: Boolean = true,
  val success: Boolean = false,
  val message: String = "",
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val loginRepository: LoginRepository,
) : ViewModel() {
  private val _uiState = MutableStateFlow(SettingsUiState())
  val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

  fun reload() {
    fetchData()
  }

  private fun fetchData() {
    _uiState.update {
      it.copy(
        isLoading = true,
        success = false,
      )
    }

    viewModelScope.launch {
      val userDataFlow = loginRepository.userData

      userDataFlow
        .catch { exception ->
          val message = exception.message
          _uiState.update {
            it.copy(
              message = message ?: "Unknown Error",
              isLoading = false,
            )
          }
        }
        .collect { userData ->
          _uiState.update {
            it.copy(
              userData = userData,
              success = true,
              isLoading = false,
            )
          }
        }
    }
  }

  fun logout() {
    _uiState.update {
      it.copy(isLoading = true)
    }

    viewModelScope.launch {
      val booleanFlow = loginRepository.logout()

      booleanFlow
        .catch { exception ->
          val message = exception.message
          if (BuildConfig.DEBUG) {
            _uiState.update { it.copy(message = message ?: "Unknown Error") }
          }
          _uiState.update { it.copy(isLoading = false) }
        }
        .collect {
          _uiState.update {
            it.copy(
              isLoading = false,
            )
          }
        }
    }
  }

  fun updateMessage(newMessage: String) {
    _uiState.update {
      it.copy(message = newMessage)
    }
  }

  fun snackbarMessageShown() {
    _uiState.update { it.copy(message = "") }
  }
}
