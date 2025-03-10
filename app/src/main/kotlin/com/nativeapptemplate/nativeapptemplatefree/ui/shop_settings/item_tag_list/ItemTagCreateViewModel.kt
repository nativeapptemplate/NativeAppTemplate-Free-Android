package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagBody
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagBodyDetail
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagCreateRoute
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemTagCreateUiState(
  val queueNumber: String = "",
  val maximumQueueNumberLength: Int = -1,
  val isCreated: Boolean = false,

  val isLoading: Boolean = true,
  val success: Boolean = false,
  val message: String = "",
)

@HiltViewModel
class ItemTagCreateViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val loginRepository: LoginRepository,
  private val itemTagRepository: ItemTagRepository
) : ViewModel() {
  private val shopId = savedStateHandle.toRoute<ItemTagCreateRoute>().shopId

  private val _uiState = MutableStateFlow(ItemTagCreateUiState())
  val uiState: StateFlow<ItemTagCreateUiState> = _uiState.asStateFlow()

  fun reload() {
    fetchData()
  }

  private fun fetchData() {
    _uiState.update {
      it.copy(
        isLoading = true,
        success = false,
        isCreated = false,
      )
    }

    viewModelScope.launch {
      val maximumQueueNumberLengthFlow = loginRepository.getMaximumQueueNumberLength()

      maximumQueueNumberLengthFlow
        .catch { exception ->
          val message = exception.message
          _uiState.update {
            it.copy(
              message = message ?: "Unknown Error",
              isLoading = false,
            )
          }
        }
        .collect { maximumQueueNumberLength ->
          _uiState.update {
            it.copy(
              maximumQueueNumberLength = maximumQueueNumberLength,
              success = true,
              isLoading = false,
            )
          }
        }
    }
  }

  fun createItemTag() {
    _uiState.update {
      it.copy(
        isLoading = true,
        isCreated = false,
      )
    }

    viewModelScope.launch {
      val itemTagBodyDetail = ItemTagBodyDetail(
        queueNumber = uiState.value.queueNumber,
      )
      val itemTagBody = ItemTagBody(itemTagBodyDetail)

      val itemTagFlow: Flow<ItemTag> = itemTagRepository.createItemTag(shopId, itemTagBody)

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
        .collect {
          _uiState.update {
            it.copy(
              isCreated = true,
            )
          }
        }
    }
  }

  fun hasInvalidData() : Boolean {
    return hasInvalidDataQueueNumber()
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
  }
}
