package com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nativeapptemplate.nativeapptemplatefree.common.errors.codedDescription
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepository
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTags
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail.navigation.ShopDetailRoute
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

data class ShopDetailUiState(
  val isLoading: Boolean = true,
  val success: Boolean = false,
  val message: String = "",
  val shop: Shop = Shop(),
  val itemTags: ItemTags = ItemTags(),
)

/**
 * ViewModel for library view
 */
@HiltViewModel
class ShopDetailViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val shopRepository: ShopRepository,
  private val itemTagRepository: ItemTagRepository,
) : ViewModel() {
  private val shopId = savedStateHandle.toRoute<ShopDetailRoute>().id
  private val _uiState = MutableStateFlow(ShopDetailUiState())
  val uiState: StateFlow<ShopDetailUiState> = _uiState.asStateFlow()

  fun reload() {
    fetchData(shopId)
  }

  private fun fetchData(shopId: String) {
    _uiState.update {
      it.copy(
        isLoading = true,
        success = false,
      )
    }

    viewModelScope.launch {
      val shopFlow: Flow<Shop> = shopRepository.getShop(shopId)
      val itemTagsFlow: Flow<ItemTags> = itemTagRepository.getItemTags(shopId)

      combine(
        shopFlow,
        itemTagsFlow,
      ) { shop, itemTags ->
        _uiState.update {
          it.copy(
            shop = shop,
            itemTags = itemTags,
            success = true,
            isLoading = false,
          )
        }
      }.catch { exception ->
        val message = exception.codedDescription
        _uiState.update {
          it.copy(
            message = message,
            isLoading = false,
          )
        }
      }.collect {
      }
    }
  }

  fun completeItemTag(itemTagId: String) {
    _uiState.update { it.copy(isLoading = true) }

    viewModelScope.launch {
      val itemTagFlow: Flow<ItemTag> = itemTagRepository.completeItemTag(itemTagId)

      itemTagFlow
        .catch { exception ->
          _uiState.update {
            it.copy(
              message = exception.codedDescription,
              isLoading = false,
            )
          }
        }
        .collect {
          _uiState.update { it.copy(isLoading = false) }
          reload()
        }
    }
  }

  fun idleItemTag(itemTagId: String) {
    _uiState.update { it.copy(isLoading = true) }

    viewModelScope.launch {
      val itemTagFlow: Flow<ItemTag> = itemTagRepository.idleItemTag(itemTagId)

      itemTagFlow
        .catch { exception ->
          _uiState.update {
            it.copy(
              message = exception.codedDescription,
              isLoading = false,
            )
          }
        }
        .collect {
          _uiState.update { it.copy(isLoading = false) }
          reload()
        }
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
