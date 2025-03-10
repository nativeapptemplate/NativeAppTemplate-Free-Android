package com.nativeapptemplate.nativeapptemplatefree.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.ui.graphics.vector.ImageVector
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.ui.scan.navigation.ScanBaseRoute
import com.nativeapptemplate.nativeapptemplatefree.ui.scan.navigation.ScanRoute
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.navigation.SettingBaseRoute
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.navigation.SettingsRoute
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.navigation.ShopBaseRoute
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.navigation.ShopsRoute
import kotlin.reflect.KClass

/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
enum class TopLevelDestination(
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val iconTextId: Int,
  val route: KClass<*>,
  val baseRoute: KClass<*> = route,
) {
  SHOPS_TAB(
    selectedIcon = Icons.Rounded.Storefront,
    unselectedIcon = Icons.Outlined.Storefront,
    iconTextId = R.string.title_shops,
    route = ShopsRoute::class,
    baseRoute = ShopBaseRoute::class,
  ),
  SCAN_TAB(
    selectedIcon = Icons.Rounded.PhoneAndroid,
    unselectedIcon = Icons.Outlined.PhoneAndroid,
    iconTextId = R.string.title_scan,
    route = ScanRoute::class,
    baseRoute = ScanBaseRoute::class,
  ),
  SETTINGS_TAB(
    selectedIcon = Icons.Rounded.Settings,
    unselectedIcon = Icons.Outlined.Settings,
    iconTextId = R.string.title_settings,
    route = SettingsRoute::class,
    baseRoute = SettingBaseRoute::class,
  ),
}
