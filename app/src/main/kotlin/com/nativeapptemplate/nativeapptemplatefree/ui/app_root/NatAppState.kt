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
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.navigation.navigateToSettings
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.navigation.TopLevelDestination
import com.nativeapptemplate.nativeapptemplatefree.navigation.TopLevelDestination.SCAN_TAB
import com.nativeapptemplate.nativeapptemplatefree.navigation.TopLevelDestination.SETTINGS_TAB
import com.nativeapptemplate.nativeapptemplatefree.navigation.TopLevelDestination.SHOPS_TAB
import com.nativeapptemplate.nativeapptemplatefree.ui.scan.navigation.navigateToScan
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.navigation.navigateToShopList
import com.nativeapptemplate.nativeapptemplatefree.utils.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Composable
fun rememberNatAppState(
  loginRepository: LoginRepository,
  networkMonitor: NetworkMonitor,
  coroutineScope: CoroutineScope = rememberCoroutineScope(),
  navController: NavHostController = rememberNavController(),
): NatAppState {
//  NavigationTrackingSideEffect(navController)
  return remember(
    loginRepository,
    navController,
    coroutineScope,
    networkMonitor,
  ) {
    NatAppState(
      loginRepository = loginRepository,
      navController = navController,
      coroutineScope = coroutineScope,
      networkMonitor = networkMonitor,
    )
  }
}

@Stable
class NatAppState(
  val loginRepository: LoginRepository,
  val navController: NavHostController,
  val coroutineScope: CoroutineScope,
  networkMonitor: NetworkMonitor,
) {
  val currentDestination: NavDestination?
    @Composable get() = navController
      .currentBackStackEntryAsState().value?.destination

  val shouldShowBottomBar: StateFlow<Boolean> = combine(
    loginRepository.isLoggedIn(),
    loginRepository.shouldUpdateApp(),
    loginRepository.shouldUpdatePrivacy(),
    loginRepository.shouldUpdateTerms()
  ) {
      theIsLoggedIn,
      theShouldUpdateApp,
      theShouldUpdatePrivacy,
      theShouldUpdateTerms ->
    theIsLoggedIn && !theShouldUpdateApp && !theShouldUpdatePrivacy && !theShouldUpdateTerms
  }.stateIn(
    scope = coroutineScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = false,
  )

  val isLoggedIn = loginRepository.isLoggedIn()
    .stateIn(
      scope = coroutineScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = false,
    )

  val shouldUpdateApp = loginRepository.shouldUpdateApp()
    .stateIn(
      scope = coroutineScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = false,
    )

  val shouldUpdatePrivacy = loginRepository.shouldUpdatePrivacy()
    .stateIn(
      scope = coroutineScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = false,
    )

  val shouldUpdateTerms = loginRepository.shouldUpdateTerms()
    .stateIn(
      scope = coroutineScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = false,
    )

  val isEmailUpdated = loginRepository.isEmailUpdated()
    .stateIn(
      scope = coroutineScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = false,
    )

  val isMyAccountDeleted = loginRepository.isMyAccountDeleted()
    .stateIn(
      scope = coroutineScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = false,
    )

  val shouldNavigateToScanView = loginRepository.shouldNavigateToScanView()
    .stateIn(
      scope = coroutineScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = false,
    )

  val isShopDeleted = loginRepository.isShopDeleted()
    .stateIn(
      scope = coroutineScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = false,
    )

  val isOffline = networkMonitor.isOnline
    .map(Boolean::not)
    .stateIn(
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
    updateShouldNavigateToScanViewToFalse()

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
        SCAN_TAB -> navController.navigateToScan(topLevelNavOptions)
        SETTINGS_TAB -> navController.navigateToSettings(topLevelNavOptions)
      }
    }
  }

  fun navigateToScan() {
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

    navController.navigateToScan(topLevelNavOptions)
  }

  private fun updateShouldNavigateToScanViewToFalse() {
    coroutineScope.launch {
      loginRepository.setShouldNavigateToScanView(false)
    }
  }
}
