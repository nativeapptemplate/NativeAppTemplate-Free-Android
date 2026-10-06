package com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.navigation.navigateToShopList

/**
 * Where the app should be, given the signed-in state and any pending required updates.
 */
internal enum class AuthDestination {
  ONBOARDING,
  NEED_APP_UPDATES,
  ACCEPT_PRIVACY,
  ACCEPT_TERMS,
  SHOPS,
}

internal fun authDestination(
  isLoggedIn: Boolean,
  shouldUpdateApp: Boolean,
  shouldUpdatePrivacy: Boolean,
  shouldUpdateTerms: Boolean,
): AuthDestination = when {
  !isLoggedIn -> AuthDestination.ONBOARDING
  shouldUpdateApp -> AuthDestination.NEED_APP_UPDATES
  shouldUpdatePrivacy -> AuthDestination.ACCEPT_PRIVACY
  shouldUpdateTerms -> AuthDestination.ACCEPT_TERMS
  else -> AuthDestination.SHOPS
}

/**
 * Navigates to [destination] when it changes, replacing the whole back stack so Back can never
 * return across the signed-in / signed-out boundary.
 *
 * A null [destination] means the signed-in state has not loaded yet, so nothing happens.
 * The last destination routed to is saved, so an Activity recreation with an unchanged
 * destination leaves the user on the screen they were on.
 */
@Composable
internal fun AuthRoutingEffect(
  navController: NavController,
  destination: AuthDestination?,
) {
  var routedTo by rememberSaveable { mutableStateOf<AuthDestination?>(null) }

  LaunchedEffect(destination) {
    if (destination == null || destination == routedTo) return@LaunchedEffect

    val replaceBackStack = navOptions {
      popUpTo(navController.graph.id) { inclusive = true }
      launchSingleTop = true
    }
    when (destination) {
      AuthDestination.ONBOARDING -> navController.navigateToOnboarding(replaceBackStack)
      AuthDestination.NEED_APP_UPDATES -> navController.navigateToNeedAppUpdates(replaceBackStack)
      AuthDestination.ACCEPT_PRIVACY -> navController.navigateToAcceptPrivacy(replaceBackStack)
      AuthDestination.ACCEPT_TERMS -> navController.navigateToAcceptTerms(replaceBackStack)
      AuthDestination.SHOPS -> navController.navigateToShopList(replaceBackStack)
    }
    routedTo = destination
  }
}
