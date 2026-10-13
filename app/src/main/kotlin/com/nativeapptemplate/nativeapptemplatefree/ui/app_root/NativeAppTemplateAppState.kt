package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.tracing.trace
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.navigation.TopLevelDestination
import com.nativeapptemplate.nativeapptemplatefree.navigation.TopLevelDestination.SETTINGS_TAB
import com.nativeapptemplate.nativeapptemplatefree.navigation.TopLevelDestination.SHOPS_TAB
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.AuthDestination
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.authDestination
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.navigation.navigateToSettings
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.navigation.navigateToShopList
import com.nativeapptemplate.nativeapptemplatefree.utils.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberNativeAppTemplateAppState(
  loginRepository: LoginRepository,
  networkMonitor: NetworkMonitor,
  coroutineScope: CoroutineScope = rememberCoroutineScope(),
  navController: NavHostController = rememberNavController(),
): NativeAppTemplateAppState {
  return remember(
    loginRepository,
    navController,
    coroutineScope,
    networkMonitor,
  ) {
    NativeAppTemplateAppState(
      loginRepository = loginRepository,
      navController = navController,
      coroutineScope = coroutineScope,
      networkMonitor = networkMonitor,
    )
  }
}

@Stable
class NativeAppTemplateAppState(
  val loginRepository: LoginRepository,
  val navController: NavHostController,
  val coroutineScope: CoroutineScope,
  networkMonitor: NetworkMonitor,
) {
  val currentDestination: NavDestination?
    @Composable get() = navController
      .currentBackStackEntryAsState().value?.destination

  /**
   * Where the app should route to. Null until the signed-in state has loaded, so the app does not
   * route to onboarding on the initial `false` values before DataStore emits.
   */
  internal val authDestination: StateFlow<AuthDestination?> = combine(
    loginRepository.isLoggedIn(),
    loginRepository.shouldUpdateApp(),
    loginRepository.shouldUpdatePrivacy(),
    loginRepository.shouldUpdateTerms(),
  ) { isLoggedIn, shouldUpdateApp, shouldUpdatePrivacy, shouldUpdateTerms ->
    authDestination(isLoggedIn, shouldUpdateApp, shouldUpdatePrivacy, shouldUpdateTerms)
  }.stateIn(
    scope = coroutineScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = null,
  )

  /** The tabs show only on the signed-in screens, not while a required update is pending. */
  val shouldShowBottomBar = authDestination.map { it == AuthDestination.SHOPS }.asState()

  val isOffline = networkMonitor.isOnline.map(Boolean::not).asState()

  private fun Flow<Boolean>.asState(): StateFlow<Boolean> = stateIn(
    scope = coroutineScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = false,
  )

  /**
   * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
   * route.
   */
  val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

  /**
   * UI logic for navigating to a top level destination in the app. Top level destinations have
   * only one copy of the destination of the back stack, and save and restore state whenever you
   * navigate to and from it.
   *
   * @param topLevelDestination: The destination the app needs to navigate to.
   */
  fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
    trace("Navigation: ${topLevelDestination.name}") {
      val topLevelNavOptions = navOptions {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(navController.graph.findStartDestination().id) {
          saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
      }

      when (topLevelDestination) {
        SHOPS_TAB -> navController.navigateToShopList(topLevelNavOptions)
        SETTINGS_TAB -> navController.navigateToSettings(topLevelNavOptions)
      }
    }
  }
}
