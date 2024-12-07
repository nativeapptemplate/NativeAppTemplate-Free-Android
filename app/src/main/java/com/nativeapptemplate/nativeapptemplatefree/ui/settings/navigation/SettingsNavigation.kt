package com.nativeapptemplate.nativeapptemplatefree.ui.settings.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.PasswordEditView
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.SettingsView
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.ShopkeeperEditView

const val SETTINGS_ROUTE = "settings_tab_route"
const val SHOPKEEPER_EDIT_ROUTE = "settings_tab_shopkeeper_edit_route"
const val PASSWORD_EDIT_ROUTE = "settings_tab_password_edit_route"

fun NavController.navigateToSettings(navOptions: NavOptions) = navigate(SETTINGS_ROUTE, navOptions)

fun NavGraphBuilder.settingsView(
  onShowShopkeeperEditClick: () -> Unit,
  onShowPasswordEditClick: () -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
) {
  composable(route = SETTINGS_ROUTE) {
    SettingsView(
      onShowShopkeeperEditClick = onShowShopkeeperEditClick,
      onShowPasswordEditClick = onShowPasswordEditClick,
      onShowSnackbar = onShowSnackbar,
    )
  }
}

fun NavController.navigateToShopkeeperEdit() {
  navigate(SHOPKEEPER_EDIT_ROUTE)
}

fun NavGraphBuilder.shopkeeperEditView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable(
    route = SHOPKEEPER_EDIT_ROUTE,
    ) {
    ShopkeeperEditView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavController.navigateToPasswordEdit() {
  navigate(PASSWORD_EDIT_ROUTE)
}

fun NavGraphBuilder.passwordEditView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable(
    route = PASSWORD_EDIT_ROUTE,
  ) {
    PasswordEditView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}