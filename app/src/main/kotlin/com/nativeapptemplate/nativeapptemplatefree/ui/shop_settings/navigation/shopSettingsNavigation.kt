package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.NumberTagsWebpageListView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.ShopBasicSettingsView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.ShopSettingsView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail.ItemTagDetailView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail.ItemTagEditView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail.ItemTagWriteView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list.ItemTagCreateView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list.ItemTagListView
import kotlinx.serialization.Serializable

@Serializable data class ShopSettingsRoute(val id: String)
@Serializable data class ShopBasicSettingsRoute(val id: String)
@Serializable data class NumberTagsWebpageListRoute(val id: String)
@Serializable data class ItemTagListRoute(val shopId: String)
@Serializable data class ItemTagCreateRoute(val shopId: String)
@Serializable data class ItemTagDetailRoute(val id: String)
@Serializable data class ItemTagEditRoute(val id: String)
@Serializable data class ItemTagWriteRoute(val id: String, val isLock: Boolean, val itemTagType: String)

fun NavController.navigateToShopSettings(shopId: String, navOptions: NavOptionsBuilder.() -> Unit = {}) {
  navigate(route = ShopSettingsRoute(shopId)) {
    navOptions()
  }
}

fun NavGraphBuilder.shopSettingsView(
  onShowBasicSettingsClick: (String) -> Unit,
  onShowItemTagListClick: (String) -> Unit,
  onShowNumberTagsWebpageListClick: (String) -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable<ShopSettingsRoute> {
    ShopSettingsView(
      onShowBasicSettingsClick = onShowBasicSettingsClick,
      onShowItemTagListClick = onShowItemTagListClick,
      onShowNumberTagsWebpageListClick = onShowNumberTagsWebpageListClick,
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

fun NavController.navigateToNumberTagsWebpageList(shopId: String, navOptions: NavOptionsBuilder.() -> Unit = {}) {
  navigate(route = NumberTagsWebpageListRoute(shopId)) {
    navOptions()
  }
}

fun NavGraphBuilder.numberTagsWebpageListView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable<NumberTagsWebpageListRoute> {
    NumberTagsWebpageListView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavController.navigateToItemTagList(shopId: String, navOptions: NavOptionsBuilder.() -> Unit = {}) {
  navigate(route = ItemTagListRoute(shopId)) {
    navOptions()
  }
}

fun NavGraphBuilder.itemTagListView(
  onItemClick: (String) -> Unit,
  onAddItemTagClick: (String) -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable<ItemTagListRoute> {
    ItemTagListView(
      onItemClick = onItemClick,
      onAddItemTagClick = onAddItemTagClick,
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavController.navigateToItemTagCreate(shopId: String, navOptions: NavOptionsBuilder.() -> Unit = {}) {
  navigate(route = ItemTagCreateRoute(shopId)) {
    navOptions()
  }
}

fun NavGraphBuilder.itemTagCreateView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable<ItemTagCreateRoute> {
    ItemTagCreateView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavController.navigateToItemTagDetail(itemTagId: String, navOptions: NavOptionsBuilder.() -> Unit = {}) {
  navigate(route = ItemTagDetailRoute(itemTagId)) {
    navOptions()
  }
}

fun NavGraphBuilder.itemTagDetailView(
  onShowItemTagEditClick: (String) -> Unit,
  onShowItemTagWriteClick: (String, Boolean, String) -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable<ItemTagDetailRoute> {
    ItemTagDetailView(
      onShowItemTagEditClick = onShowItemTagEditClick,
      onShowItemTagWriteClick = onShowItemTagWriteClick,
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavController.navigateToItemTagEdit(itemTagId: String, navOptions: NavOptionsBuilder.() -> Unit = {}) {
  navigate(route = ItemTagEditRoute(itemTagId)) {
    navOptions()
  }
}

fun NavGraphBuilder.itemTagEditView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable<ItemTagEditRoute> {
    ItemTagEditView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavController.navigateToItemTagWrite(
  itemTagId: String,
  isLock: Boolean,
  itemTagType: String,
  navOptions: NavOptionsBuilder.() -> Unit = {}
) {
  navigate(route = ItemTagWriteRoute(itemTagId, isLock, itemTagType)) {
    navOptions()
  }
}

fun NavGraphBuilder.itemTagWriteView(
  onBackClick: () -> Unit,
) {
  dialog<ItemTagWriteRoute> {
    ItemTagWriteView(
      onBackClick = onBackClick,
    )
  }
}
