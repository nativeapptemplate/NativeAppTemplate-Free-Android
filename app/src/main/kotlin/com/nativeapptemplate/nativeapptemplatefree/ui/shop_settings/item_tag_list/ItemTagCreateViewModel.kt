package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nativeapptemplate.nativeapptemplatefree.NatConstants
import com.nativeapptemplate.nativeapptemplatefree.common.errors.codedDescription
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagBody
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagBodyDetail
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagCreateRoute
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
  val name: String = "",
  val description: String = "",
  val maximumNameLength: Int = NatConstants.MAXIMUM_ITEM_TAG_NAME_LENGTH,
  val isCreated: Boolean = false,

  val isLoading: Boolean = false,
  val success: Boolean = true,
  val message: String = "",
)

@HiltViewModel
class ItemTagCreateViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val itemTagRepository: ItemTagRepository,
) : ViewModel() {
  private val shopId = savedStateHandle.toRoute<ItemTagCreateRoute>().shopId

  private val _uiState = MutableStateFlow(ItemTagCreateUiState())
  val uiState: StateFlow<ItemTagCreateUiState> = _uiState.asStateFlow()

  fun reload() {
    _uiState.update {
      ItemTagCreateUiState()
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
        name = uiState.value.name,
        description = uiState.value.description,
      )
      val itemTagBody = ItemTagBody(itemTagBodyDetail)

      val itemTagFlow: Flow<ItemTag> = itemTagRepository.createItemTag(shopId, itemTagBody)

      itemTagFlow
        .catch { exception ->
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
              isCreated = true,
            )
          }
        }
    }
  }

  fun hasInvalidData(): Boolean {
    return hasInvalidDataName() || hasInvalidDataDescription()
  }

  fun hasInvalidDataName(): Boolean {
    val name = uiState.value.name
    val maximumNameLength = uiState.value.maximumNameLength

    if (name.isBlank()) return true
    if (maximumNameLength <= 0) return false
    return name.length > maximumNameLength
  }

  fun hasInvalidDataDescription(): Boolean {
    return uiState.value.description.length > NatConstants.MAXIMUM_ITEM_TAG_DESCRIPTION_LENGTH
  }

  fun updateName(newName: String) {
    val maximumNameLength = uiState.value.maximumNameLength
    if (maximumNameLength > 0 && newName.length > maximumNameLength) return

    _uiState.update {
      it.copy(name = newName)
    }
  }

  fun updateDescription(newDescription: String) {
    if (newDescription.length > NatConstants.MAXIMUM_ITEM_TAG_DESCRIPTION_LENGTH) return

    _uiState.update {
      it.copy(description = newDescription)
    }
  }

  fun snackbarMessageShown() {
    _uiState.update { it.copy(message = "") }
  }
}
