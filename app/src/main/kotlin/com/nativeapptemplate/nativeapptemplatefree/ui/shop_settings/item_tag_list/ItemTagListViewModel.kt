package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nativeapptemplate.nativeapptemplatefree.common.errors.codedDescription
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepository
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagListRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
  val itemTags: List<Data> = emptyList(),

  val isLoading: Boolean = true,
  val success: Boolean = false,
  val message: String = "",

  val currentPage: Int = 1,
  val totalPages: Int = 1,
  val isLoadingMore: Boolean = false,
) {
  val hasMorePages: Boolean get() = currentPage < totalPages
}

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

  private var fetchJob: Job? = null

  fun reload() {
    fetchData(page = 1, isReload = true)
  }

  fun isEmpty(): StateFlow<Boolean> = uiState.map { it.itemTags.isEmpty() }
    .stateIn(
      scope = viewModelScope,
      initialValue = false,
      started = SharingStarted.WhileSubscribed(5_000),
    )

  fun loadMore() {
    val state = _uiState.value
    if (state.isLoadingMore || !state.hasMorePages) return
    fetchData(page = state.currentPage + 1, isReload = false)
  }

  private fun fetchData(page: Int, isReload: Boolean) {
    val shopId = shopId

    if (isReload) {
      _uiState.update {
        it.copy(
          isLoading = true,
          success = false,
        )
      }
    } else {
      _uiState.update {
        it.copy(
          isLoadingMore = true,
        )
      }
    }

    fetchJob?.cancel()
    fetchJob = viewModelScope.launch {
      val shopFlow: Flow<Shop> = shopRepository.getShop(shopId)
      val itemTagsFlow = itemTagRepository.getItemTags(shopId, page)

      combine(
        shopFlow,
        itemTagsFlow,
      ) { shop, itemTags ->
        val newItems = itemTags.getDatumWithRelationships()
        val allItems = if (isReload) newItems else _uiState.value.itemTags + newItems

        _uiState.update {
          it.copy(
            shop = shop,
            itemTags = allItems,
            currentPage = itemTags.meta?.currentPage ?: page,
            totalPages = itemTags.meta?.totalPages ?: 1,
            isLoadingMore = false,
            success = true,
            isLoading = false,
          )
        }
      }.catch { exception ->
        val message = exception.codedDescription
        _uiState.update {
          it.copy(
            message = message,
            isLoading = false,
            isLoadingMore = false,
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
