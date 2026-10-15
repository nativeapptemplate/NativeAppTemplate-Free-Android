package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepository
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ErrorShowingUiState
import com.nativeapptemplate.nativeapptemplatefree.ui.common.catchShowingError
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ShopSettingsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShopSettingsUiState(
  val shop: Shop = Shop(),
  val isShopDeleted: Boolean = false,

  val isLoading: Boolean = true,
  val success: Boolean = false,
  val message: String = "",
) : ErrorShowingUiState<ShopSettingsUiState> {
  override fun showingError(message: String) = copy(message = message, isLoading = false)
}

@HiltViewModel
class ShopSettingsViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val loginRepository: LoginRepository,
  private val shopRepository: ShopRepository,
) : ViewModel() {
  private val shopId = savedStateHandle.toRoute<ShopSettingsRoute>().id

  private val _uiState = MutableStateFlow(ShopSettingsUiState())

  /** The current load; cancelled before the next one so collectors of never-ending flows do not pile up. */
  private var loadJob: Job? = null
  val uiState: StateFlow<ShopSettingsUiState> = _uiState.asStateFlow()

  fun reload() {
    fetchData(shopId)
  }

  private fun fetchData(shopId: String) {
    _uiState.update {
      it.copy(
        isLoading = true,
        message = "",
        success = false,
        isShopDeleted = false,
      )
    }

    loadJob?.cancel()
    loadJob = viewModelScope.launch {
      val shopFlow: Flow<Shop> = shopRepository.getShop(shopId)

      shopFlow
        .catchShowingError(_uiState)
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
        .catchShowingError(_uiState)
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
