package com.nativeapptemplate.nativeapptemplatefree.ui.settings.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.PasswordEditView
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.SettingsView
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.ShopkeeperEditView
import kotlinx.serialization.Serializable

@Serializable data object SettingBaseRoute
@Serializable data object SettingsRoute
@Serializable data object ShopkeeperEditRoute
@Serializable data object PasswordEditRoute

fun NavGraphBuilder.settingBaseView(
  destination: NavGraphBuilder.() -> Unit,
) {
  navigation<SettingBaseRoute>(startDestination = SettingsRoute) {
    destination()
  }
}

fun NavController.navigateToSettings(navOptions: NavOptions) = navigate(route = SettingsRoute, navOptions)

fun NavGraphBuilder.settingsView(
  onShowShopkeeperEditClick: () -> Unit,
  onShowPasswordEditClick: () -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
) {
  composable<SettingsRoute> {
    SettingsView(
      onShowShopkeeperEditClick = onShowShopkeeperEditClick,
      onShowPasswordEditClick = onShowPasswordEditClick,
      onShowSnackbar = onShowSnackbar,
    )
  }
}

fun NavController.navigateToShopkeeperEdit() = navigate(ShopkeeperEditRoute)

fun NavGraphBuilder.shopkeeperEditView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable<ShopkeeperEditRoute> {
    ShopkeeperEditView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavController.navigateToPasswordEdit() = navigate(PasswordEditRoute)

fun NavGraphBuilder.passwordEditView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable<PasswordEditRoute> {
    PasswordEditView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}