package com.nativeapptemplate.nativeapptemplatefree

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.MainActivityUiState.Loading
import com.nativeapptemplate.nativeapptemplatefree.MainActivityUiState.Success
import com.nativeapptemplate.nativeapptemplatefree.common.coroutines.runCatchingCancellable
import com.nativeapptemplate.nativeapptemplatefree.data.login.AppLanguageSyncer
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.data.login.SessionLifecycle
import com.nativeapptemplate.nativeapptemplatefree.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  private val loginRepository: LoginRepository,
  private val appLanguageSyncer: AppLanguageSyncer,
  private val sessionLifecycle: SessionLifecycle,
) : ViewModel() {

  val uiState: StateFlow<MainActivityUiState> = loginRepository.userData.map {
    Success(it)
  }.stateIn(
    scope = viewModelScope,
    initialValue = Loading,
    started = SharingStarted.WhileSubscribed(5_000),
  )

  fun updateDidShowTapShopBelowTip(didShowTapShopBelowTip: Boolean) {
    viewModelScope.launch {
      loginRepository.setDidShowTapShopBelowTip(didShowTapShopBelowTip)
    }
  }

  /** Sends the app language as the shopkeeper's locale if it changed (e.g. in the system settings). */
  fun syncAppLanguage() {
    viewModelScope.launch {
      appLanguageSyncer.sync()
    }
  }

  fun updatePermissions() {
    viewModelScope.launch {
      if (!loginRepository.isLoggedIn().first()) return@launch

      runCatchingCancellable { sessionLifecycle.refreshPermissions() }
        .onFailure { Log.e("MainActivityViewModel", "Failed to update permissions", it) }
    }
  }
}

sealed interface MainActivityUiState {
  data object Loading : MainActivityUiState
  data class Success(val userData: UserData) : MainActivityUiState {
    override val isLoggedIn = userData.isLoggedIn
  }

  val isLoggedIn: Boolean get() = false
}
