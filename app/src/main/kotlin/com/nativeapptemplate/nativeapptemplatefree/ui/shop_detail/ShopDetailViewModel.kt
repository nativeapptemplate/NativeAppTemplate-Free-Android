package com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepository
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTags
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ErrorShowingUiState
import com.nativeapptemplate.nativeapptemplatefree.ui.common.catchShowingError
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail.navigation.ShopDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
) : ErrorShowingUiState<ShopDetailUiState> {
  override fun showingError(message: String) = copy(message = message, isLoading = false)
}

@HiltViewModel
class ShopDetailViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val shopRepository: ShopRepository,
  private val itemTagRepository: ItemTagRepository,
) : ViewModel() {
  private val shopId = savedStateHandle.toRoute<ShopDetailRoute>().id
  private val _uiState = MutableStateFlow(ShopDetailUiState())

  /** The current load; cancelled before the next one so collectors of never-ending flows do not pile up. */
  private var loadJob: Job? = null
  val uiState: StateFlow<ShopDetailUiState> = _uiState.asStateFlow()

  fun reload() {
    fetchData(shopId)
  }

  private fun fetchData(shopId: String) {
    _uiState.update {
      it.copy(
        isLoading = true,
        message = "",
        success = false,
      )
    }

    loadJob?.cancel()
    loadJob = viewModelScope.launch {
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
      }.catchShowingError(_uiState).collect {
      }
    }
  }

  fun completeItemTag(itemTagId: String) {
    _uiState.update { it.copy(isLoading = true) }

    viewModelScope.launch {
      val itemTagFlow: Flow<ItemTag> = itemTagRepository.completeItemTag(itemTagId)

      itemTagFlow
        .catchShowingError(_uiState)
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
        .catchShowingError(_uiState)
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
