package com.nativeapptemplate.nativeapptemplatefree.ui.shops.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.ShopCreateView
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.ShopListView

const val SHOPS_ROUTE = "shops_tab_route"
const val SHOP_CREATE_ROUTE = "shops_tab_shop_create_route"

fun NavController.navigateToShopList(navOptions: NavOptions? = null) {
  navigate(SHOPS_ROUTE, navOptions)
}

fun NavGraphBuilder.shopListView(
  onItemClick: (String) -> Unit,
  onAddShopClick: () -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
) {
  composable(
    route = SHOPS_ROUTE,
  ) {
    ShopListView(
      onItemClick = onItemClick,
      onAddShopClick = onAddShopClick,
      onShowSnackbar = onShowSnackbar,
    )
  }
}

fun NavController.navigateToShopCreate() = navigate(SHOP_CREATE_ROUTE)

fun NavGraphBuilder.shopCreateView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable(SHOP_CREATE_ROUTE) {
    ShopCreateView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}
