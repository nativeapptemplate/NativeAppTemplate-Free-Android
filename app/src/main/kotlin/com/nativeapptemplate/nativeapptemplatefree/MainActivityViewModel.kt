package com.nativeapptemplate.nativeapptemplatefree

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.MainActivityUiState.Loading
import com.nativeapptemplate.nativeapptemplatefree.MainActivityUiState.Success
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import com.nativeapptemplate.nativeapptemplatefree.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  private val loginRepository: LoginRepository,
) : ViewModel() {

  val uiState: StateFlow<MainActivityUiState> = loginRepository.userData.map {
    Success(it)
  }.stateIn(
    scope = viewModelScope,
    initialValue = Loading,
    started = SharingStarted.WhileSubscribed(5_000),
  )

  fun updatePermissions() {
    viewModelScope.launch {
      val isLoggedIn = loginRepository.isLoggedIn().first()

      if (isLoggedIn) {
        val permissionsFlow: Flow<Permissions> = loginRepository.getPermissions()

        permissionsFlow
          .catch { exception ->
            Log.e("MainActivityViewModel", "Failed to update permissions", exception)

            val booleanFlow = loginRepository.logout()
            booleanFlow
              .catch { logoutException ->
                Log.e("MainActivityViewModel", "Logout error", logoutException)
              }
              .collect {
              }
          }
          .collect { permissions ->
            loginRepository.setPermissions(permissions)
          }
      }
    }
  }

  fun isLoggedIn(): StateFlow<Boolean> = loginRepository
    .isLoggedIn()
    .stateIn(
      scope = viewModelScope,
      initialValue = false,
      started = SharingStarted.WhileSubscribed(5_000),
    )
}

sealed interface MainActivityUiState {
  data object Loading : MainActivityUiState
  data class Success(val userData: UserData) : MainActivityUiState
}
