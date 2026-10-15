package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.NativeAppTemplateConstants
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.data.login.SessionLifecycle
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Login
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ErrorShowingUiState
import com.nativeapptemplate.nativeapptemplatefree.ui.common.catchShowingError
import com.nativeapptemplate.nativeapptemplatefree.ui.common.showError
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignInEmailAndPasswordUiState(
  val email: String = "",
  val password: String = "",

  val loggedInShopkeeper: LoggedInShopkeeper = LoggedInShopkeeper(),

  val isLoading: Boolean = false,
  val message: String = "",
) : ErrorShowingUiState<SignInEmailAndPasswordUiState> {
  override fun showingError(message: String) = copy(message = message, isLoading = false)
}

@HiltViewModel
class SignInEmailAndPasswordViewModel @Inject constructor(
  private val loginRepository: LoginRepository,
  private val sessionLifecycle: SessionLifecycle,
) : ViewModel() {
  private val _uiState = MutableStateFlow(SignInEmailAndPasswordUiState())
  val uiState: StateFlow<SignInEmailAndPasswordUiState> = _uiState.asStateFlow()

  fun login() {
    _uiState.update {
      it.copy(
        isLoading = true,
        // A new attempt: the last one's error is stale (its snackbar closes).
        message = "",
      )
    }

    viewModelScope.launch {
      val login = Login(uiState.value.email, uiState.value.password)
      val loggedInShopkeeperFlow: Flow<LoggedInShopkeeper> = loginRepository.login(login)

      loggedInShopkeeperFlow
        .catchShowingError(_uiState)
        .collect { loggedInShopkeeper ->
          _uiState.update { it.copy(loggedInShopkeeper = loggedInShopkeeper) }
          try {
            sessionLifecycle.start(loggedInShopkeeper)
            _uiState.update { it.copy(isLoading = false) }
          } catch (cancellation: CancellationException) {
            throw cancellation
          } catch (exception: Exception) {
            // MainActivityViewModel fetches the permissions again on the next resume.
            Log.e("SignInEmailAndPasswordViewModel", "Failed to update permissions", exception)
            _uiState.showError(exception)
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
