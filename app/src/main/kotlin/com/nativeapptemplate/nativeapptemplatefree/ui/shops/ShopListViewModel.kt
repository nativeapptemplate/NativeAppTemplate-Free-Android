package com.nativeapptemplate.nativeapptemplatefree.ui.shops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepository
import com.nativeapptemplate.nativeapptemplatefree.model.Shops
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShopListUiState(
  val shops: Shops = Shops(),

  val isLoading: Boolean = true,
  val success: Boolean = false,
  val message: String = "",
)

/**
 * ViewModel for library view
 */
@HiltViewModel
class ShopListViewModel @Inject constructor(
  private val loginRepository: LoginRepository,
  private val shopRepository: ShopRepository
) : ViewModel() {
  private val _uiState = MutableStateFlow(ShopListUiState())
  val uiState: StateFlow<ShopListUiState> = _uiState.asStateFlow()

  fun reload() = fetchData()

  fun isLoggedIn(): StateFlow<Boolean> = loginRepository
    .isLoggedIn()
    .stateIn(
      scope = viewModelScope,
      initialValue = false,
      started = SharingStarted.WhileSubscribed(5_000),
    )

  fun isEmpty(): StateFlow<Boolean> = uiState.map { it.shops.datum.isEmpty() }
    .stateIn(
      scope = viewModelScope,
      initialValue = false,
      started = SharingStarted.WhileSubscribed(5_000),
    )

  private fun fetchData() {
    _uiState.update {
      it.copy(
        isLoading = true,
        success = false,
      )
    }

    viewModelScope.launch {
      val shopsFlow: Flow<Shops> = shopRepository.getShops()

      shopsFlow
        .catch { exception ->
          val message = exception.message
          _uiState.update {
            it.copy(
              message = message ?: "Unknown Error",
              isLoading = false,
            )
          }
        }
        .collect { shops ->
          _uiState.update {
            it.copy(
              shops = shops,
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

