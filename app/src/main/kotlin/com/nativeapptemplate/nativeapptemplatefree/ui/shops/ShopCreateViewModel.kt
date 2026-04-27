package com.nativeapptemplate.nativeapptemplatefree.ui.shops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.NatConstants
import com.nativeapptemplate.nativeapptemplatefree.common.errors.codedDescription
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepository
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.model.ShopBody
import com.nativeapptemplate.nativeapptemplatefree.model.ShopBodyDetail
import com.nativeapptemplate.nativeapptemplatefree.model.TimeZones
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShopCreateUiState(
  val name: String = "",
  val description: String = "",
  val timeZone: String = TimeZones.currentTimeZoneKey(),
  val maximumNameLength: Int = NatConstants.MAXIMUM_SHOP_NAME_LENGTH,
  val maximumDescriptionLength: Int = NatConstants.MAXIMUM_SHOP_DESCRIPTION_LENGTH,

  val isLoading: Boolean = false,
  val isCreated: Boolean = false,
  val message: String = "",
)

@HiltViewModel
class ShopCreateViewModel @Inject constructor(
  private val shopRepository: ShopRepository,
) : ViewModel() {
  private val _uiState = MutableStateFlow(ShopCreateUiState())
  val uiState: StateFlow<ShopCreateUiState> = _uiState.asStateFlow()

  fun createShop() {
    _uiState.update {
      it.copy(isLoading = true)
    }

    viewModelScope.launch {
      val shopBodyDetail = ShopBodyDetail(
        name = uiState.value.name,
        description = uiState.value.description,
        timeZone = uiState.value.timeZone,
      )
      val shopBody = ShopBody(shopBodyDetail)

      val shopFlow: Flow<Shop> = shopRepository.createShop(shopBody)

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
        .collect {
          _uiState.update {
            it.copy(
              isCreated = true,
              isLoading = false,
            )
          }
        }
    }
  }

  fun hasInvalidData(): Boolean {
    return hasInvalidDataName() || hasInvalidDataDescription()
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
