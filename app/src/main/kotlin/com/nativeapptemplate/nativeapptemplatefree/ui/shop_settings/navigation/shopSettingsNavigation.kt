package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.ShopBasicSettingsView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.ShopSettingsView
import kotlinx.serialization.Serializable

@Serializable data class ShopSettingsRoute(val id: String)
@Serializable data class ShopBasicSettingsRoute(val id: String)

fun NavController.navigateToShopSettings(shopId: String, navOptions: NavOptionsBuilder.() -> Unit = {}) {
  navigate(route = ShopSettingsRoute(shopId)) {
    navOptions()
  }
}

fun NavGraphBuilder.shopSettingsView(
  onShowBasicSettingsClick: (String) -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable<ShopSettingsRoute> {
    ShopSettingsView(
      onShowBasicSettingsClick = onShowBasicSettingsClick,
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavController.navigateToShopBasicSettings(shopId: String, navOptions: NavOptionsBuilder.() -> Unit = {}) {
  navigate(route = ShopBasicSettingsRoute(shopId)) {
    navOptions()
  }
}
fun NavGraphBuilder.shopBasicSettingsView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable<ShopBasicSettingsRoute> {
    ShopBasicSettingsView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}
