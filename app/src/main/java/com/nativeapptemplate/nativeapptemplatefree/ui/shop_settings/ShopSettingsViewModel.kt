package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepository
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ShopSettingsArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShopSettingsUiState(
  val shop: Shop = Shop(),
  val isShopDeleted: Boolean = false,

  val isLoading: Boolean = true,
  val success: Boolean = false,
  val message: String = "",
)

@HiltViewModel
class ShopSettingsViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val loginRepository: LoginRepository,
  private val shopRepository: ShopRepository,

  ) : ViewModel() {
  private val shopSettingsArgs: ShopSettingsArgs = ShopSettingsArgs(savedStateHandle)

  private val _uiState = MutableStateFlow(ShopSettingsUiState())
  val uiState: StateFlow<ShopSettingsUiState> = _uiState.asStateFlow()

  fun reload() {
    fetchData(shopSettingsArgs.shopId)
  }

  private fun fetchData(shopId: String) {
    _uiState.update {
      it.copy(
        isLoading = true,
        success = false,
        isShopDeleted = false,
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

  fun deleteShop(shopId: String) {
    _uiState.update {
      it.copy(
        isLoading = true,
        isShopDeleted = false,
      )
    }

    viewModelScope.launch {
      val booleanFlow: Flow<Boolean> = shopRepository.deleteShop(shopId)

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
          loginRepository.setIsShopDeleted(true)

          _uiState.update {
            it.copy(
              isShopDeleted = true,
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

