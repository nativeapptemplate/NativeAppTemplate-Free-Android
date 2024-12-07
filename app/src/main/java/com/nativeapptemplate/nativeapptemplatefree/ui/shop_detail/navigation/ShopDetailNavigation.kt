package com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail.ShopDetailView
import java.net.URLDecoder

private val URL_CHARACTER_ENCODING = Charsets.UTF_8.name()

const val SHOP_ID_ARG = "shopId"
const val SHOP_DETAIL_ROUTE_BASE = "shops_tab_shop_detail_route"
const val SHOP_DETAIL_ROUTE = "$SHOP_DETAIL_ROUTE_BASE/{$SHOP_ID_ARG}"

internal class ShopDetailArgs(val shopId: String) {
  constructor(savedStateHandle: SavedStateHandle) :
          this(URLDecoder.decode(checkNotNull(savedStateHandle[SHOP_ID_ARG]), URL_CHARACTER_ENCODING))
}

fun NavController.navigateToShopDetail(shopId: String) {
  navigate("$SHOP_DETAIL_ROUTE_BASE/$shopId")
}

fun NavGraphBuilder.shopDetailView(
  onSettingsClick: (String) -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable(SHOP_DETAIL_ROUTE) {
    ShopDetailView(
      onSettingsClick = onSettingsClick,
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

