package com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
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
  val didShowReadInstructionsTip: Boolean = false,
)

/**
 * ViewModel for library view
 */
@HiltViewModel
class ShopDetailViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val loginRepository: LoginRepository,
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
      val didShowReadInstructionsTipFlow = loginRepository.didShowReadInstructionsTip()

      combine(
        shopFlow,
        itemTagsFlow,
        didShowReadInstructionsTipFlow,
      ) { shop,
          itemTags,
          didShowReadInstructionsTip, ->
        _uiState.update {
          it.copy(
            shop = shop,
            itemTags = itemTags,
            didShowReadInstructionsTip = didShowReadInstructionsTip,
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

  fun completeItemTag(itemTagId: String) {
    _uiState.update {
      it.copy(
        isLoading = true,
      )
    }

    viewModelScope.launch {
      val itemTagFlow: Flow<ItemTag> = itemTagRepository.completeItemTag(itemTagId)

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
              isLoading = false,
            )
          }

          reload()
        }
    }
  }

  fun resetItemTag(itemTagId: String) {
    _uiState.update {
      it.copy(
        isLoading = true,
      )
    }

    viewModelScope.launch {
      val itemTagFlow: Flow<ItemTag> = itemTagRepository.resetItemTag(itemTagId)

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
              isLoading = false,
            )
          }

          reload()
        }
    }
  }

  fun updateMessage(newMessage: String) {
    _uiState.update {
      it.copy(message = newMessage)
    }
  }

  fun updateDidShowReadInstructionsTip(didShowReadInstructionsTip: Boolean) {
    viewModelScope.launch {
      loginRepository.setDidShowReadInstructionsTip(didShowReadInstructionsTip)
    }
  }

  fun snackbarMessageShown() {
    _uiState.update { it.copy(message = "") }
  }
}
