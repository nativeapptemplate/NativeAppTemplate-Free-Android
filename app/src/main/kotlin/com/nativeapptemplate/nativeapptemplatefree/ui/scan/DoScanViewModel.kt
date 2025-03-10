package com.nativeapptemplate.nativeapptemplatefree.ui.scan

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.model.CompleteScanResult
import com.nativeapptemplate.nativeapptemplatefree.model.CompleteScanResultType
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagInfoFromNdefMessage
import com.nativeapptemplate.nativeapptemplatefree.model.ShowTagInfoScanResult
import com.nativeapptemplate.nativeapptemplatefree.model.ShowTagInfoScanResultType
import com.nativeapptemplate.nativeapptemplatefree.ui.scan.navigation.DoScanRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DoScanUiState(
  val isScanned: Boolean = false,
  val isLoading: Boolean = false,
)

@HiltViewModel
class DoScanViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val loginRepository: LoginRepository,
  ) : ViewModel() {
  val isTest = savedStateHandle.toRoute<DoScanRoute>().isTest

  private val _uiState = MutableStateFlow(DoScanUiState())
  val uiState: StateFlow<DoScanUiState> = _uiState.asStateFlow()

  fun updateItemTagInfoFromNdefMessage(
    itemTagInfoFromNdefMessage: ItemTagInfoFromNdefMessage,
  ) {
    _uiState.update { it.copy(isLoading = true) }

    viewModelScope.launch {
      if (isTest) {
        val showTagInfoScanResult = ShowTagInfoScanResult()
        showTagInfoScanResult.itemTagInfoFromNdefMessage = itemTagInfoFromNdefMessage

        if (!showTagInfoScanResult.itemTagInfoFromNdefMessage.success) {
          showTagInfoScanResult.message = itemTagInfoFromNdefMessage.message
          showTagInfoScanResult.showTagInfoScanResultType = ShowTagInfoScanResultType.Failed
        }

        try {
          loginRepository.setShowTagInfoScanResult(showTagInfoScanResult)
        } catch (exception: Exception) {
          val message = exception.message
          showTagInfoScanResult.message = message ?: "Unknown Error"
          showTagInfoScanResult.showTagInfoScanResultType = ShowTagInfoScanResultType.Failed

          loginRepository.setShowTagInfoScanResult(showTagInfoScanResult)
        } finally {
          _uiState.update { it.copy(isLoading = false) }
        }
      } else {
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
        } finally {
          _uiState.update { it.copy(isLoading = false) }
        }
      }
    }
  }

  fun updateIsScanned(newIsScanned: Boolean) {
    _uiState.update {
      it.copy(isScanned = newIsScanned)
    }
  }

  fun updateScanViewSelectedTabIndex(
  ) {
    val scanViewSelectedTabIndex = if (isTest) 1 else 0
    _uiState.update { it.copy(isLoading = true) }

    viewModelScope.launch {
      try {
        loginRepository.setScanViewSelectedTabIndex(scanViewSelectedTabIndex)
      } catch (exception: Exception) {
        Log.e("DoScanViewModel", "Failed to update scanViewSelectedTabIndex", exception)
      } finally {
        _uiState.update { it.copy(isLoading = false) }
      }
    }
  }

  fun updateExecFlagAfterScanning(execFlagAfterScanning: Boolean) {
    if (isTest) {
      updateShouldFetchItemTagForShowTagInfoScan(execFlagAfterScanning)
    } else {
      updateShouldCompleteItemTagForCompleteScan(execFlagAfterScanning)
    }
  }

  private fun updateShouldFetchItemTagForShowTagInfoScan(shouldFetchItemTagForShowTagInfoScan: Boolean) {
    viewModelScope.launch {
      loginRepository.setShouldFetchItemTagForShowTagInfoScan(shouldFetchItemTagForShowTagInfoScan)
    }
  }

  private fun updateShouldCompleteItemTagForCompleteScan(shouldCompleteItemTagForCompleteScan: Boolean) {
    viewModelScope.launch {
      loginRepository.setShouldCompleteItemTagForCompleteScan(shouldCompleteItemTagForCompleteScan)
    }
  }
}

