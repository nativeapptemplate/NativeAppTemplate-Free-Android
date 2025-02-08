package com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail.ShopDetailView
import kotlinx.serialization.Serializable

@Serializable data class ShopDetailRoute(val id: String)

fun NavController.navigateToShopDetail(shopId: String, navOptions: NavOptionsBuilder.() -> Unit = {}) {
  navigate(route = ShopDetailRoute(shopId)) {
    navOptions()
  }
}

fun NavGraphBuilder.shopDetailView(
  onSettingsClick: (String) -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable<ShopDetailRoute> {
    ShopDetailView(
      onSettingsClick = onSettingsClick,
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}
