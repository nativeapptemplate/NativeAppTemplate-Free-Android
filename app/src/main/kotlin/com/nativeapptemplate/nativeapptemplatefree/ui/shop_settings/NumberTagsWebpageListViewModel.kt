package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepository
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.NumberTagsWebpageListRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NumberTagsWebpageListUiState(
  val isLoading: Boolean = true,
  val success: Boolean = false,
  val message: String = "",
  val shop: Shop = Shop(),
)

/**
 * ViewModel for library view
 */
@HiltViewModel
class NumberTagsWebpageListViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val shopRepository: ShopRepository,

  ) : ViewModel() {
  private val shopId = savedStateHandle.toRoute<NumberTagsWebpageListRoute>().id

  private val _uiState = MutableStateFlow(NumberTagsWebpageListUiState())
  val uiState: StateFlow<NumberTagsWebpageListUiState> = _uiState.asStateFlow()

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

      shopFlow
        .catch { exception ->
          val message = exception.message
          _uiState.update {
            it.copy(
              message = message ?: "Unknown Error",
              isLoading = false,
            )
          }
        }
        .collect { shop ->
          _uiState.update {
            it.copy(
              shop = shop,
              success = true,
              isLoading = false,
            )
          }
        }
    }
  }

  fun snackbarMessageShown() {
    _uiState.update { it.copy(message = "") }
  }
}

