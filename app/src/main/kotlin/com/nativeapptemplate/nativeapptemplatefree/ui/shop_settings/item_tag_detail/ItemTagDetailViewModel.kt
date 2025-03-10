package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemTagDetailUiState(
  val itemTag: ItemTag = ItemTag(),

  val isLock: Boolean = false,

  val isDeleted: Boolean = false,

  val isLoading: Boolean = true,
  val success: Boolean = false,
  val message: String = "",
)

@HiltViewModel
class ItemTagDetailViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val itemTagRepository: ItemTagRepository,
) : ViewModel() {
  val itemTagId = savedStateHandle.toRoute<ItemTagDetailRoute>().id

  private val _uiState = MutableStateFlow(ItemTagDetailUiState())
  val uiState: StateFlow<ItemTagDetailUiState> = _uiState.asStateFlow()

  fun reload() {
    fetchData()
  }

  private fun fetchData() {
    _uiState.update {
      it.copy(
        isLoading = true,
        success = false,
        isDeleted = false,
      )
    }

    viewModelScope.launch {
      val itemTagFlow: Flow<ItemTag> = itemTagRepository.getItemTag(itemTagId)

      itemTagFlow
        .catch { exception ->
          val message = exception.message
          _uiState.update {
            it.copy(
              message = message ?: "Unknown Error",
              isLoading = false,
            )
          }
        }
        .collect { itemTag ->
          _uiState.update {
            it.copy(
              itemTag = itemTag,
              success = true,
              isLoading = false,
            )
          }
        }
    }
  }

  fun deleteItemTag() {
    _uiState.update {
      it.copy(
        isLoading = true,
        isDeleted = false,
      )
    }

    viewModelScope.launch {
      val booleanFlow: Flow<Boolean> = itemTagRepository.deleteItemTag(itemTagId)

      booleanFlow
        .catch { exception ->
          val message = exception.message
          _uiState.update {
            it.copy(
              message = message ?: "Unknown Error",
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
        }
    }
  }

  fun updateIsLock(newIsLock: Boolean) {
    _uiState.update {
      it.copy(isLock = newIsLock)
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

