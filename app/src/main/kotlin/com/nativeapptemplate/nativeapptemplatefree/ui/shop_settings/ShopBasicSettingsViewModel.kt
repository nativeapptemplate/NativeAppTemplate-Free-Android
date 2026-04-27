package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nativeapptemplate.nativeapptemplatefree.NatConstants
import com.nativeapptemplate.nativeapptemplatefree.common.errors.codedDescription
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepository
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.model.ShopUpdateBody
import com.nativeapptemplate.nativeapptemplatefree.model.ShopUpdateBodyDetail
import com.nativeapptemplate.nativeapptemplatefree.model.TimeZones
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ShopBasicSettingsRoute
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
  val maximumNameLength: Int = NatConstants.MAXIMUM_SHOP_NAME_LENGTH,
  val maximumDescriptionLength: Int = NatConstants.MAXIMUM_SHOP_DESCRIPTION_LENGTH,

  val isLoading: Boolean = true,
  val success: Boolean = false,
  val message: String = "",

  val isUpdated: Boolean = false,
)

@HiltViewModel
class ShopBasicSettingsViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val shopRepository: ShopRepository,
) : ViewModel() {
  private val shopId = savedStateHandle.toRoute<ShopBasicSettingsRoute>().id

  private val _uiState = MutableStateFlow(ShopBasicSettingsUiState())
  val uiState: StateFlow<ShopBasicSettingsUiState> = _uiState.asStateFlow()

  fun reload() {
    fetchData(shopId)
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
          val message = exception.codedDescription
          _uiState.update {
            it.copy(
              message = message,
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

      val shopFlow: Flow<Shop> = shopRepository.updateShop(shopId, shopUpdateBody)

      shopFlow
        .catch { exception ->
          val message = exception.codedDescription
          _uiState.update {
            it.copy(
              message = message,
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

  fun hasInvalidData(): Boolean {
    if (hasInvalidDataName()) return true
    if (hasInvalidDataDescription()) return true

    val shopData = uiState.value.shop.getData()!!

    return shopData.getName() == uiState.value.name &&
      shopData.getDescription() == uiState.value.description &&
      shopData.getTimeZone() == uiState.value.timeZone
  }

  fun hasInvalidDataName(): Boolean {
    val name = uiState.value.name
    val maximumNameLength = uiState.value.maximumNameLength
    return name.isBlank() || name.length > maximumNameLength
  }

  fun hasInvalidDataDescription(): Boolean {
    return uiState.value.description.length > uiState.value.maximumDescriptionLength
  }

  fun updateName(newName: String) {
    if (newName.length <= uiState.value.maximumNameLength) {
      _uiState.update {
        it.copy(name = newName)
      }
    }
  }

  fun updateDescription(newDescription: String) {
    if (newDescription.length <= uiState.value.maximumDescriptionLength) {
      _uiState.update {
        it.copy(description = newDescription)
      }
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
