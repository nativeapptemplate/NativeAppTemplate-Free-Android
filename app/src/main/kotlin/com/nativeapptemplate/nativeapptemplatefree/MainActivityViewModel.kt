package com.nativeapptemplate.nativeapptemplatefree

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.MainActivityUiState.Loading
import com.nativeapptemplate.nativeapptemplatefree.MainActivityUiState.Success
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.model.CompleteScanResult
import com.nativeapptemplate.nativeapptemplatefree.model.CompleteScanResultType
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagInfoFromNdefMessage
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import com.nativeapptemplate.nativeapptemplatefree.model.ShowTagInfoScanResult
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

  fun updateShouldFetchItemTagForShowTagInfoScan(shouldFetchItemTagForShowTagInfoScan: Boolean) {
    viewModelScope.launch {
      loginRepository.setShouldFetchItemTagForShowTagInfoScan(shouldFetchItemTagForShowTagInfoScan)
    }
  }

  fun updateShouldCompleteItemTagForCompleteScan(shouldCompleteItemTagForCompleteScan: Boolean) {
    viewModelScope.launch {
      loginRepository.setShouldCompleteItemTagForCompleteScan(shouldCompleteItemTagForCompleteScan)
    }
  }

  fun updateShouldNavigateToScanView(shouldNavigateToScanView: Boolean) {
    viewModelScope.launch {
      loginRepository.setShouldNavigateToScanView(shouldNavigateToScanView)
    }
  }

  fun initScanViewSelectedTabIndex() {
    viewModelScope.launch {
      loginRepository.setScanViewSelectedTabIndex(0)
    }
  }

  fun initShowTagInfoScanResult() {
    viewModelScope.launch {
      loginRepository.setShowTagInfoScanResult(ShowTagInfoScanResult())
    }
  }

  fun initCompleteScanResult() {
    viewModelScope.launch {
      loginRepository.setCompleteScanResult(CompleteScanResult())
    }
  }

  fun updateDidShowTapShopBelowTip(didShowTapShopBelowTip: Boolean) {
    viewModelScope.launch {
      loginRepository.setDidShowTapShopBelowTip(didShowTapShopBelowTip)
    }
  }

  fun updateDidShowReadInstructionsTip(didShowReadInstructionsTip: Boolean) {
    viewModelScope.launch {
      loginRepository.setDidShowReadInstructionsTip(didShowReadInstructionsTip)
    }
  }

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

  fun updateItemTagInfoFromNdefMessage(
    itemTagInfoFromNdefMessage: ItemTagInfoFromNdefMessage,
  ) {
    viewModelScope.launch {
      val completeScanResult = CompleteScanResult()
      completeScanResult.itemTagInfoFromNdefMessage = itemTagInfoFromNdefMessage

      if (!completeScanResult.itemTagInfoFromNdefMessage.success) {
        completeScanResult.message = itemTagInfoFromNdefMessage.message
        completeScanResult.completeScanResultType = CompleteScanResultType.Failed
      }

      try {
        loginRepository.setCompleteScanResult(completeScanResult)
      } catch (exception: Exception) {
        val message = exception.message
        completeScanResult.message = message ?: "Unknown Error"
        completeScanResult.completeScanResultType = CompleteScanResultType.Failed

        loginRepository.setCompleteScanResult(completeScanResult)
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
  data class Success(val userData: UserData) : MainActivityUiState {
    override val isLoggedIn = userData.isLoggedIn
  }

  val isLoggedIn: Boolean get() = false
}
