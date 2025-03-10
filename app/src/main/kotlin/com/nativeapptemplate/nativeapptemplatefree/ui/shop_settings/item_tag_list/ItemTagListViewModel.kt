package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepository
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTags
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagListRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemTagListUiState(
  val shop: Shop = Shop(),
  val itemTags: ItemTags = ItemTags(),

  val isLoading: Boolean = true,
  val success: Boolean = false,
  val message: String = "",
)

/**
 * ViewModel for library view
 */
@HiltViewModel
class ItemTagListViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val shopRepository: ShopRepository,
  private val itemTagRepository: ItemTagRepository,
) : ViewModel() {
  val shopId = savedStateHandle.toRoute<ItemTagListRoute>().shopId

  private val _uiState = MutableStateFlow(ItemTagListUiState())
  val uiState: StateFlow<ItemTagListUiState> = _uiState.asStateFlow()

  fun reload() {
    fetchData()
  }

  fun isEmpty(): StateFlow<Boolean> = uiState.map { it.itemTags.datum.isEmpty() }
    .stateIn(
      scope = viewModelScope,
      initialValue = false,
      started = SharingStarted.WhileSubscribed(5_000),
    )

  private fun fetchData() {
    val shopId = shopId

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
        shopFlow, itemTagsFlow
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

  fun deleteItemTag(itemTagId: String) {
    _uiState.update {
      it.copy(
        isLoading = true,
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
              isLoading = false,
            )
          }
          reload()
        }
    }
  }

  fun snackbarMessageShown() {
    _uiState.update { it.copy(message = "") }
  }
}

