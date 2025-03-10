package com.nativeapptemplate.nativeapptemplatefree.ui.scan.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import com.nativeapptemplate.nativeapptemplatefree.ui.scan.DoScanView
import com.nativeapptemplate.nativeapptemplatefree.ui.scan.ScanView
import kotlinx.serialization.Serializable

@Serializable data object ScanBaseRoute
@Serializable data object ScanRoute
@Serializable data class DoScanRoute(val isTest: Boolean)

fun NavGraphBuilder.scanBaseView(
  destination: NavGraphBuilder.() -> Unit,
) {
  navigation<ScanBaseRoute>(startDestination = ScanRoute) {
    destination()
  }
}

fun NavController.navigateToScan(navOptions: NavOptions? = null) = navigate(route = ScanRoute, navOptions)

fun NavGraphBuilder.scanView(
  onShowDoScanViewClick: (Boolean) -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
) {
  composable<ScanRoute> {
    ScanView(
      onShowDoScanViewClick = onShowDoScanViewClick,
      onShowSnackbar = onShowSnackbar,
    )
  }
}

fun NavController.navigateToDoScan(isTest: Boolean, navOptions: NavOptionsBuilder.() -> Unit = {}) {
  navigate(route = DoScanRoute(isTest)) {
    navOptions()
  }
}

fun NavGraphBuilder.doScanView(
  onBackClick: () -> Unit,
) {
  dialog<DoScanRoute>(
    dialogProperties = DialogProperties(
      usePlatformDefaultWidth = false,
      dismissOnBackPress = false,
      dismissOnClickOutside = false,
    )
  ) {
    DoScanView(
      onBackClick = onBackClick,
    )
  }
}
