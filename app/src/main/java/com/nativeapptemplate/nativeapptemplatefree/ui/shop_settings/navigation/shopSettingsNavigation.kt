package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.ShopBasicSettingsView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.ShopSettingsView
import java.net.URLDecoder

private val URL_CHARACTER_ENCODING = Charsets.UTF_8.name()

const val SHOP_ID_ARG = "shopId"
const val SHOP_SETTINGS_ROUTE_BASE = "shops_tab_shop_settings_route"
const val SHOP_SETTINGS_ROUTE = "$SHOP_SETTINGS_ROUTE_BASE/{$SHOP_ID_ARG}"

const val SHOP_BASIC_SETTINGS_ROUTE_BASE = "shops_tab_shop_basic_settings_route"
const val SHOP_BASIC_SETTINGS_ROUTE = "$SHOP_BASIC_SETTINGS_ROUTE_BASE/{$SHOP_ID_ARG}"

internal class ShopSettingsArgs(val shopId: String) {
  constructor(savedStateHandle: SavedStateHandle) :
          this(URLDecoder.decode(checkNotNull(savedStateHandle[SHOP_ID_ARG]), URL_CHARACTER_ENCODING))
}

internal class ShopBasicSettingsArgs(val shopId: String) {
  constructor(savedStateHandle: SavedStateHandle) :
          this(URLDecoder.decode(checkNotNull(savedStateHandle[SHOP_ID_ARG]), URL_CHARACTER_ENCODING))
}

fun NavController.navigateToShopSettings(shopId: String) {
  navigate("$SHOP_SETTINGS_ROUTE_BASE/$shopId")
}

fun NavGraphBuilder.shopSettingsView(
  onShowBasicSettingsClick: (String) -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable(
    route = SHOP_SETTINGS_ROUTE,
    arguments = listOf(
      navArgument(SHOP_ID_ARG) { type = NavType.StringType },
    ),

    ) {
    ShopSettingsView(
      onShowBasicSettingsClick = onShowBasicSettingsClick,
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavController.navigateToShopBasicSettings(shopId: String) {
  navigate("$SHOP_BASIC_SETTINGS_ROUTE_BASE/$shopId")
}

fun NavGraphBuilder.shopBasicSettingsView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable(
    route = SHOP_BASIC_SETTINGS_ROUTE,
    arguments = listOf(
      navArgument(SHOP_ID_ARG) { type = NavType.StringType },
    ),

    ) {
    ShopBasicSettingsView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}
