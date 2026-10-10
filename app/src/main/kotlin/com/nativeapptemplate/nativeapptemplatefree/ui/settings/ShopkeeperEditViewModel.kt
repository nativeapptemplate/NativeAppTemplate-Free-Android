package com.nativeapptemplate.nativeapptemplatefree.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.common.errors.codedDescription
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.data.login.SignUpRepository
import com.nativeapptemplate.nativeapptemplatefree.model.Locales
import com.nativeapptemplate.nativeapptemplatefree.model.SignUpForUpdate
import com.nativeapptemplate.nativeapptemplatefree.model.TimeZones
import com.nativeapptemplate.nativeapptemplatefree.model.UserData
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
  val locale: String = Locales.DEFAULT,

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
  private val signUpRepository: SignUpRepository,
) : ViewModel() {
  private val _uiState = MutableStateFlow(ShopkeeperEditUiState())

  /** The current load; cancelled before the next one so collectors of never-ending flows do not pile up. */
  private var loadJob: Job? = null
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

    loadJob?.cancel()
    loadJob = viewModelScope.launch {
      val userDataFlow = loginRepository.userData
      // userData re-emits on every DataStore write (MainActivity writes one on every recreation),
      // so fill the form only from the first value of this load to keep unsaved edits.
      var hasFilledForm = false

      userDataFlow
        .catch { exception ->
          val message = exception.codedDescription
          _uiState.update {
            it.copy(
              message = message,
              isLoading = false,
            )
          }
        }
        .collect { userData ->
          if (!uiState.value.isUpdated && !uiState.value.isEmailUpdated && !uiState.value.isDeleted) {
            _uiState.update {
              it.copy(
                userData = userData,
                name = if (hasFilledForm) it.name else userData.name,
                email = if (hasFilledForm) it.email else userData.email,
                timeZone = if (hasFilledForm) it.timeZone else userData.timeZone,
                locale = if (hasFilledForm) it.locale else userData.locale,
                success = true,
                isLoading = false,
              )
            }
            hasFilledForm = true
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
        locale = uiState.value.locale,
      )

      val loggedInShopkeeperFlow = signUpRepository.updateAccount(signUpForUpdate)

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
          // The account still exists on the server, so keep the session and let the user retry.
          val message = exception.codedDescription
          _uiState.update {
            it.copy(
              message = message,
              isLoading = false,
            )
          }
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

  fun hasInvalidData(): Boolean {
    if (uiState.value.name.isBlank()) return true

    if (hasInvalidDataEmail()) return true

    val userData = uiState.value.userData

    return userData.name == uiState.value.name &&
      userData.email == uiState.value.email &&
      userData.timeZone == uiState.value.timeZone &&
      userData.locale == uiState.value.locale
  }

  fun hasInvalidDataEmail(): Boolean {
    if (uiState.value.email.isBlank()) return true

    return !uiState.value.email.isValidEmail()
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

  fun updateLocale(newLocale: String) {
    _uiState.update {
      it.copy(locale = newLocale)
    }
  }

  fun snackbarMessageShown() {
    _uiState.update { it.copy(message = "") }
  }
}
