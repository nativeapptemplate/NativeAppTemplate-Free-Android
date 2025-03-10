package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagWriteRoute
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ItemTagWriteUiState(
  val isUpdated: Boolean = false,
  val isFailed: Boolean = false,

  val message: String = "",
)

@HiltViewModel
class ItemTagWriteViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,

  ) : ViewModel() {
  private val itemTagId = savedStateHandle.toRoute<ItemTagWriteRoute>().id
  val isLock = savedStateHandle.toRoute<ItemTagWriteRoute>().isLock
  val itemTagType = savedStateHandle.toRoute<ItemTagWriteRoute>().itemTagType

  private val _uiState = MutableStateFlow(ItemTagWriteUiState())
  val uiState: StateFlow<ItemTagWriteUiState> = _uiState.asStateFlow()

  val scanUri = Utility.scanUri(
    itemTagId = itemTagId,
    itemTagType = itemTagType
  )

  fun updateIsUpdated(newIsUpdated: Boolean) {
    _uiState.update {
      it.copy(isUpdated = newIsUpdated)
    }
  }

  fun updateIsFailed(newIsFailed: Boolean) {
    _uiState.update {
      it.copy(isFailed = newIsFailed)
    }
  }

  fun updateMessage(newMessage: String) {
    _uiState.update {
      it.copy(message = newMessage)
    }
  }
}

