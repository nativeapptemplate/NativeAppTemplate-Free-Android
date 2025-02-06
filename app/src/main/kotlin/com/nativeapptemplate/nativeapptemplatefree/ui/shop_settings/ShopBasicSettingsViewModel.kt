package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepository
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.model.ShopUpdateBody
import com.nativeapptemplate.nativeapptemplatefree.model.ShopUpdateBodyDetail
import com.nativeapptemplate.nativeapptemplatefree.model.TimeZones
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ShopBasicSettingsArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShopBasicSettingsUiState(
  val shop: Shop = Shop(),

  val name: String = "",
  val description: String = "",
  val timeZone: String = TimeZones.DEFAULT_TIME_ZONE,

  val isLoading: Boolean = true,
  val success: Boolean = false,
  val message: String = "",

  val isUpdated: Boolean = false,
)

@HiltViewModel
class ShopBasicSettingsViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val shopRepository: ShopRepository
) : ViewModel() {
  private val shopBasicSettingsArgs: ShopBasicSettingsArgs = ShopBasicSettingsArgs(savedStateHandle)

  private val _uiState = MutableStateFlow(ShopBasicSettingsUiState())
  val uiState: StateFlow<ShopBasicSettingsUiState> = _uiState.asStateFlow()

  fun reload() {
    fetchData(shopBasicSettingsArgs.shopId)
  }

  private fun fetchData(shopId: String) {
    _uiState.update {
      it.copy(
        isLoading = true,
        success = false,
        isUpdated = false,
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
              name = shop.getName(),
              description = shop.getDescription(),
              timeZone = shop.getTimeZone(),
              success = true,
              isLoading = false,
            )
          }
        }
    }
  }
  
  fun updateShop() {
    _uiState.update {
      it.copy(
        isLoading = true,
        isUpdated = false,
      )
    }

    viewModelScope.launch {
      val shopUpdateBodyDetail = ShopUpdateBodyDetail(
        name = uiState.value.name,
        description = uiState.value.description,
        timeZone = uiState.value.timeZone,
      )
      val shopUpdateBody = ShopUpdateBody(shopUpdateBodyDetail)

      val shopFlow: Flow<Shop> = shopRepository.updateShop(shopBasicSettingsArgs.shopId, shopUpdateBody)

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
              isUpdated = true,
              isLoading = false,
            )
          }
        }
    }
  }

  fun hasInvalidData() : Boolean {
    if (uiState.value.name.isBlank()) return true

    val shopData = uiState.value.shop.getData()!!

    return shopData.getName() == uiState.value.name &&
        shopData.getDescription() == uiState.value.description &&
        shopData.getTimeZone() == uiState.value.timeZone
  }

  fun updateName(newName: String) {
    _uiState.update {
      it.copy(name = newName)
    }
  }

  fun updateDescription(newDescription: String) {
    _uiState.update {
      it.copy(description = newDescription)
    }
  }

  fun updateTimeZone(newTimeZone: String) {
    _uiState.update {
      it.copy(timeZone = newTimeZone)
    }
  }

  fun snackbarMessageShown() {
    _uiState.update { it.copy(message = "") }
  }
}
