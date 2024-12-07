package com.nativeapptemplate.nativeapptemplatefree.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.data.login.SignUpRepository
import com.nativeapptemplate.nativeapptemplatefree.model.SignUpForUpdate
import com.nativeapptemplate.nativeapptemplatefree.model.TimeZones
import com.nativeapptemplate.nativeapptemplatefree.model.UserData
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility.validateEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShopkeeperEditUiState(
  val userData: UserData = UserData(),

  val name: String = "",
  val email: String = "",
  val timeZone: String = TimeZones.DEFAULT_TIME_ZONE,

  val isUpdated: Boolean = false,
  val isEmailUpdated: Boolean = false,
  val isDeleted: Boolean = false,

  val isLoading: Boolean = true,
  val success: Boolean = false,
  val message: String = "",
)

@HiltViewModel
class ShopkeeperEditViewModel @Inject constructor(
  private val loginRepository: LoginRepository,
  private val signUpRepository: SignUpRepository
) : ViewModel() {
  private val _uiState = MutableStateFlow(ShopkeeperEditUiState())
  val uiState: StateFlow<ShopkeeperEditUiState> = _uiState.asStateFlow()

  fun reload() {
    fetchData()
  }

  private fun fetchData() {
    _uiState.update {
      it.copy(
        isLoading = true,
        success = false,
        isUpdated = false,
        isEmailUpdated = false,
        isDeleted = false,
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
          if (!uiState.value.isUpdated && !uiState.value.isEmailUpdated && !uiState.value.isDeleted) {
            _uiState.update {
              it.copy(
                userData = userData,
                name = userData.name,
                email = userData.email,
                timeZone = userData.timeZone,
                success = true,
                isLoading = false,
              )
            }
          }
        }
    }
  }
  
  fun updateShopkeeper() {
    _uiState.update {
      it.copy(
        isLoading = true,
        isUpdated = false,
        isEmailUpdated = false,
      )
    }

    viewModelScope.launch {
      val emailUpdating = uiState.value.userData.email != uiState.value.email

      val signUpForUpdate = SignUpForUpdate(
        name = uiState.value.name,
        email = uiState.value.email.trim(),
        timeZone = uiState.value.timeZone,
      )

      val loggedInShopkeeperFlow = signUpRepository.updateAccount(signUpForUpdate)

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
        .collect { loggedInShopkeeper ->
          loginRepository.setShopkeeperForUpdate(loggedInShopkeeper)

          if (emailUpdating) {
            _uiState.update {
              it.copy(
                isEmailUpdated = true,
              )
            }

            val booleanFlow = loginRepository.logout()
            booleanFlow
              .catch { exception ->
                Log.e("ShopkeeperEditViewModel", "Logout error", exception)
                loginRepository.setIsEmailUpdated(true)
              }
              .collect {
                loginRepository.setIsEmailUpdated(true)
              }
          } else {
            _uiState.update {
              it.copy(
                isUpdated = true,
                isLoading = false,
              )
            }
          }
        }
    }
  }

  fun deleteShopkeeper() {
    _uiState.update {
      it.copy(
        isLoading = true,
        isDeleted = false,
      )
    }

    viewModelScope.launch {
      val booleanFlow: Flow<Boolean> = signUpRepository.deleteAccount()

      booleanFlow
        .catch { exception ->
          val message = exception.message
          _uiState.update {
            it.copy(
              message = message ?: "Unknown Error",
            )
          }
          loginRepository.clearUserPreferences()
        }
        .collect {
          _uiState.update {
            it.copy(
              isDeleted = true,
            )
          }
          loginRepository.clearUserPreferences()
          loginRepository.setIsMyAccountDeleted(true)
        }
    }
  }

  fun hasInvalidData() : Boolean {
    if (uiState.value.name.isBlank()) return true

    if (hasInvalidDataEmail()) return true

    val userData = uiState.value.userData

    return userData.name == uiState.value.name &&
            userData.email == uiState.value.email &&
            userData.timeZone == uiState.value.timeZone
  }

  fun hasInvalidDataEmail() : Boolean {
    if (uiState.value.email.isBlank()) return true

    return !uiState.value.email.validateEmail()
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

  fun updateTimeZone(newTimeZone: String) {
    _uiState.update {
      it.copy(timeZone = newTimeZone)
    }
  }

  fun snackbarMessageShown() {
    _uiState.update { it.copy(message = "") }
    _uiState.update {it.copy(success = false) }
  }
}
