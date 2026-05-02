package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nativeapptemplate.nativeapptemplatefree.NativeAppTemplateConstants
import com.nativeapptemplate.nativeapptemplatefree.common.errors.codedDescription
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagBody
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagBodyDetail
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagEditRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemTagEditUiState(
  val itemTag: ItemTag = ItemTag(),

  val name: String = "",
  val description: String = "",
  val maximumNameLength: Int = NativeAppTemplateConstants.MAXIMUM_ITEM_TAG_NAME_LENGTH,
  val isUpdated: Boolean = false,

  val isLoading: Boolean = true,
  val success: Boolean = false,
  val message: String = "",
)

@HiltViewModel
class ItemTagEditViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val itemTagRepository: ItemTagRepository,
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
        .collect { itemTag ->
          _uiState.update {
            it.copy(
              itemTag = itemTag,
              name = itemTag.getName(),
              description = itemTag.getDescription(),
              success = true,
              isLoading = false,
            )
          }
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
        name = uiState.value.name,
        description = uiState.value.description,
      )
      val itemTagBody = ItemTagBody(itemTagBodyDetail)

      val itemTagStream: Flow<ItemTag> = itemTagRepository.updateItemTag(itemTagId, itemTagBody)

      itemTagStream
        .catch { exception ->
          val message = exception.codedDescription
          _uiState.update {
            it.copy(
              message = message,
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

  fun hasInvalidData(): Boolean {
    if (hasInvalidDataName()) return true
    if (hasInvalidDataDescription()) return true

    val itemTag = uiState.value.itemTag
    val nameUnchanged = itemTag.getName() == uiState.value.name
    val descriptionUnchanged = itemTag.getDescription() == uiState.value.description
    return nameUnchanged && descriptionUnchanged
  }

  fun hasInvalidDataName(): Boolean {
    val name = uiState.value.name
    val maximumNameLength = uiState.value.maximumNameLength

    if (name.isBlank()) return true
    if (maximumNameLength <= 0) return false
    return name.length > maximumNameLength
  }

  fun hasInvalidDataDescription(): Boolean {
    return uiState.value.description.length > NativeAppTemplateConstants.MAXIMUM_ITEM_TAG_DESCRIPTION_LENGTH
  }

  fun updateName(newName: String) {
    val maximumNameLength = uiState.value.maximumNameLength
    if (maximumNameLength > 0 && newName.length > maximumNameLength) return

    _uiState.update {
      it.copy(name = newName)
    }
  }

  fun updateDescription(newDescription: String) {
    if (newDescription.length > NativeAppTemplateConstants.MAXIMUM_ITEM_TAG_DESCRIPTION_LENGTH) return

    _uiState.update {
      it.copy(description = newDescription)
    }
  }

  fun snackbarMessageShown() {
    _uiState.update { it.copy(message = "") }
    _uiState.update { it.copy(isUpdated = false) }
    _uiState.update { it.copy(success = false) }
  }
}
