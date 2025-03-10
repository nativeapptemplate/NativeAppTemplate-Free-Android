package com.nativeapptemplate.nativeapptemplatefree.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.model.CompleteScanResult
import com.nativeapptemplate.nativeapptemplatefree.model.CompleteScanResultType
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagData
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagInfoFromNdefMessage
import com.nativeapptemplate.nativeapptemplatefree.model.ShowTagInfoScanResult
import com.nativeapptemplate.nativeapptemplatefree.model.ShowTagInfoScanResultType
import com.nativeapptemplate.nativeapptemplatefree.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScanUiState(
  val userData: UserData = UserData(),
  val itemTagForShowTagInfoScan: ItemTag = ItemTag(),

  val showTagInfoScanResult: ShowTagInfoScanResult = ShowTagInfoScanResult(),
  val completeScanResult: CompleteScanResult = CompleteScanResult(),

  val isAlreadyCompleted: Boolean = false,

  val isLoading: Boolean = true,
  val success: Boolean = false,
  val message: String = "",
)

/**
 * ViewModel for library view
 */
@HiltViewModel
class ScanViewModel @Inject constructor(
  private val loginRepository: LoginRepository,
  private val itemTagRepository: ItemTagRepository,
) : ViewModel() {
  private val _uiState = MutableStateFlow(ScanUiState())
  val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

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
      val userDataFlow: Flow<UserData> = loginRepository.userData
      val showTagInfoScanResultFlow: Flow<ShowTagInfoScanResult> = loginRepository.showTagInfoScanResult()
      val completeScanResultFlow: Flow<CompleteScanResult> = loginRepository.completeScanResult()

      combine(
        userDataFlow,
        showTagInfoScanResultFlow,
        completeScanResultFlow,
      ) { array ->
        val userData = array[0] as UserData
        val showTagInfoScanResult = array[1] as ShowTagInfoScanResult
        val completeScanResult = array[2] as CompleteScanResult

        _uiState.update {
          it.copy(
            userData = userData,
            completeScanResult = completeScanResult,
            showTagInfoScanResult = showTagInfoScanResult,
            success = true,
            isLoading = false,
          )
        }
      }.catch { exception ->
        val message = exception.message
        _uiState.update {
          it.copy(
            message = message ?: "Unknown Error",
            isLoading = false,
          )
        }
      }.first()
    }
  }

  fun fetchItemTagForShowTagInfoScan(itemTagInfoFromNdefMessage: ItemTagInfoFromNdefMessage) {
    _uiState.update {
      it.copy(
        isLoading = true,
      )
    }

    viewModelScope.launch {
      val itemTagFlow: Flow<ItemTag> = itemTagRepository.getItemTag(itemTagInfoFromNdefMessage.id)

      itemTagFlow
        .catch { exception ->
          val message = exception.message
          val showTagInfoScanResult = uiState.value.showTagInfoScanResult
          showTagInfoScanResult.showTagInfoScanResultType = ShowTagInfoScanResultType.Failed
          showTagInfoScanResult.message = message ?: "Unknown Error"

          loginRepository.setShowTagInfoScanResult(showTagInfoScanResult)

          _uiState.update {
            it.copy(
              isLoading = false,
            )
          }
        }
        .collect { itemTag ->
          _uiState.update { it.copy(itemTagForShowTagInfoScan = itemTag) }

          val showTagInfoScanResult = uiState.value.showTagInfoScanResult
          showTagInfoScanResult.itemTagInfoFromNdefMessage = itemTagInfoFromNdefMessage

          val itemTagData = ItemTagData(itemTag)
          showTagInfoScanResult.itemTagData = itemTagData

          showTagInfoScanResult.showTagInfoScanResultType = ShowTagInfoScanResultType.Succeeded

          loginRepository.setShowTagInfoScanResult(showTagInfoScanResult)

          _uiState.update {
            it.copy(
              isLoading = false,
            )
          }
        }

      delay(200)
      reload()
    }
  }

  fun completeItemTag(itemTagInfoFromNdefMessage: ItemTagInfoFromNdefMessage) {
    _uiState.update {
      it.copy(
        isAlreadyCompleted = false,
        isLoading = true,
      )
    }

    viewModelScope.launch {
      val itemTagFlow: Flow<ItemTag> = itemTagRepository.completeItemTag(itemTagInfoFromNdefMessage.id)

      itemTagFlow
        .catch { exception ->
          val message = exception.message
          val completeScanResult = uiState.value.completeScanResult
          completeScanResult.completeScanResultType = CompleteScanResultType.Failed
          completeScanResult.message = message ?: "Unknown Error"

          loginRepository.setCompleteScanResult(completeScanResult)

          _uiState.update {
            it.copy(
              isLoading = false
            )
          }
        }
        .collect { itemTag ->
          val completeScanResult = uiState.value.completeScanResult
          completeScanResult.itemTagInfoFromNdefMessage = itemTagInfoFromNdefMessage

          val itemTagData = ItemTagData(itemTag)
          completeScanResult.itemTagData = itemTagData

          if (itemTagData.alreadyCompleted) {
            _uiState.update { it.copy(isAlreadyCompleted = true) }
            completeScanResult.completeScanResultType = CompleteScanResultType.Completed
          } else {
            completeScanResult.completeScanResultType = CompleteScanResultType.Completed
          }

          loginRepository.setCompleteScanResult(completeScanResult)

          _uiState.update {
            it.copy(
              isLoading = false,
            )
          }
        }

      delay(200)
      reload()
    }
  }

  fun resetItemTag(itemTagInfoFromNdefMessage: ItemTagInfoFromNdefMessage) {
    _uiState.update {
      it.copy(
        isLoading = true,
        isAlreadyCompleted = false,
      )
    }

    viewModelScope.launch {
      val itemTagFlow: Flow<ItemTag> = itemTagRepository.resetItemTag(itemTagInfoFromNdefMessage.id)

      itemTagFlow
        .catch { exception ->
          val message = exception.message
          val completeScanResult = uiState.value.completeScanResult
          completeScanResult.completeScanResultType = CompleteScanResultType.Failed
          completeScanResult.message = message ?: "Unknown Error"

          loginRepository.setCompleteScanResult(completeScanResult)

          _uiState.update {
            it.copy(
              isLoading = false,
            )
          }
        }
        .collect { itemTag ->
          val completeScanResult = uiState.value.completeScanResult
          completeScanResult.itemTagInfoFromNdefMessage = itemTagInfoFromNdefMessage

          val itemTagData = ItemTagData(itemTag)
          completeScanResult.itemTagData = itemTagData
          completeScanResult.completeScanResultType = CompleteScanResultType.Reset

          loginRepository.setCompleteScanResult(completeScanResult)

          _uiState.update {
            it.copy(
              isLoading = false,
            )
          }
        }

      delay(200)
      reload()
    }
  }

  fun updateMessage(newMessage: String) {
    _uiState.update {
      it.copy(message = newMessage)
    }
  }

  fun updateIsAlreadyCompleted(newIsAlreadyCompleted: Boolean) {
    _uiState.update {
      it.copy(isAlreadyCompleted = newIsAlreadyCompleted)
    }
  }

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

  fun snackbarMessageShown() {
    _uiState.update { it.copy(message = "") }
  }
}

