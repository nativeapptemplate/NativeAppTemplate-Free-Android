package com.nativeapptemplate.nativeapptemplatefree.ui.shops.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.ShopCreateView
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.ShopListView
import kotlinx.serialization.Serializable

@Serializable data object ShopsRoute
@Serializable data object ShopCreateRoute

fun NavController.navigateToShopList(navOptions: NavOptions? = null) = navigate(route = ShopsRoute, navOptions)

fun NavGraphBuilder.shopListView(
  onItemClick: (String) -> Unit,
  onAddShopClick: () -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
) {
  composable<ShopsRoute> {
    ShopListView(
      onItemClick = onItemClick,
      onAddShopClick = onAddShopClick,
      onShowSnackbar = onShowSnackbar,
    )
  }
}

fun NavController.navigateToShopCreate() = navigate(ShopCreateRoute)

fun NavGraphBuilder.shopCreateView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable<ShopCreateRoute> {
    ShopCreateView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}
