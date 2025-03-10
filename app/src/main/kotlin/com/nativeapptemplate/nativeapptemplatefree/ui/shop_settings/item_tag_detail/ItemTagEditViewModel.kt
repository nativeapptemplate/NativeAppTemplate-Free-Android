package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagBody
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagBodyDetail
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagEditRoute
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemTagEditUiState(
  val itemTag: ItemTag = ItemTag(),

  val queueNumber: String = "",
  val maximumQueueNumberLength: Int = -1,
  val isUpdated: Boolean = false,

  val isLoading: Boolean = true,
  val success: Boolean = false,
  val message: String = "",
)

@HiltViewModel
class ItemTagEditViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val loginRepository: LoginRepository,
  private val itemTagRepository: ItemTagRepository
) : ViewModel() {
  private val itemTagId = savedStateHandle.toRoute<ItemTagEditRoute>().id

  private val _uiState = MutableStateFlow(ItemTagEditUiState())
  val uiState: StateFlow<ItemTagEditUiState> = _uiState.asStateFlow()

  fun reload() {
    fetchData(itemTagId)
  }

  private fun fetchData(itemTagId: String) {
    _uiState.update {
      it.copy(
        isLoading = true,
        success = false,
        isUpdated = false,
      )
    }

    viewModelScope.launch {
      val itemTagFlow: Flow<ItemTag> = itemTagRepository.getItemTag(itemTagId)
      val maximumQueueNumberLengthFlow = loginRepository.getMaximumQueueNumberLength()

      combine(itemTagFlow, maximumQueueNumberLengthFlow) { itemTag, maximumQueueNumberLength ->
        _uiState.update {
          it.copy(
            itemTag = itemTag,
            queueNumber = itemTag.getQueueNumber(),
            maximumQueueNumberLength = maximumQueueNumberLength,
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
      }.collect {
      }
    }
  }

  fun updateItemTag() {
    _uiState.update {
      it.copy(
        isLoading = true,
        isUpdated = false,
      )
    }

    viewModelScope.launch {
      val itemTagBodyDetail = ItemTagBodyDetail(
        queueNumber = uiState.value.queueNumber,
      )
      val itemTagBody = ItemTagBody(itemTagBodyDetail)

      val itemTagStream: Flow<ItemTag> = itemTagRepository.updateItemTag(itemTagId, itemTagBody)

      itemTagStream
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
              isUpdated = true,
              isLoading = false,
            )
          }
        }
    }
  }

  fun hasInvalidData() : Boolean {
    if (hasInvalidDataQueueNumber()) return true

    val itemTag = uiState.value.itemTag
    return itemTag.getQueueNumber() == uiState.value.queueNumber
  }

  fun hasInvalidDataQueueNumber() : Boolean {
    val queueNumber = uiState.value.queueNumber

    if (queueNumber.isBlank()) return true

    if (!Utility.isAlphanumeric(queueNumber)) return true

    if (!(2 <= queueNumber.length && queueNumber.length <= uiState.value.maximumQueueNumberLength)) {
      return true
    }

    return false
  }

  fun updateQueueNumber(newQueueNumber: String) {
    if (newQueueNumber.length <= uiState.value.maximumQueueNumberLength) {
      _uiState.update {
        it.copy(queueNumber = newQueueNumber)
      }
    }
  }

  fun snackbarMessageShown() {
    _uiState.update { it.copy(message = "") }
    _uiState.update {it.copy(isUpdated = false) }
    _uiState.update { it.copy(success = false) }
  }
}
