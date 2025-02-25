package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.designsystem.component.NatBackground
import com.nativeapptemplate.nativeapptemplatefree.designsystem.component.NatNavigationBar
import com.nativeapptemplate.nativeapptemplatefree.designsystem.component.NatNavigationBarItem
import com.nativeapptemplate.nativeapptemplatefree.navigation.NatNavHost
import com.nativeapptemplate.nativeapptemplatefree.navigation.TopLevelDestination
import kotlin.reflect.KClass

@OptIn(
  ExperimentalComposeUiApi::class,
)
@Composable
fun NatApp(appState: NatAppState) {
  NatBackground {
    val snackbarHostState = remember { SnackbarHostState() }

    val isOffline by appState.isOffline.collectAsStateWithLifecycle()
    val isEmailUpdated by appState.isEmailUpdated.collectAsStateWithLifecycle()
    val isMyAccountDeleted by appState.isMyAccountDeleted.collectAsStateWithLifecycle()
    val isShopDeleted by appState.isShopDeleted.collectAsStateWithLifecycle()

    // If user is not connected to the internet show a snack bar to inform them.
    val notConnectedMessage = stringResource(R.string.not_connected)
    LaunchedEffect(isOffline) {
      if (isOffline) {
        snackbarHostState.showSnackbar(
          message = notConnectedMessage,
          duration = Indefinite,
        )
      }
    }

    val reconfirmMessage = stringResource(R.string.reconfirm_description)
    LaunchedEffect(isEmailUpdated) {
      if (isEmailUpdated) {
        snackbarHostState.showSnackbar(
          message = reconfirmMessage,
          actionLabel = "dismiss",
          duration = Indefinite,
        )

        appState.loginRepository.setIsEmailUpdated(false)
      }
    }

    val myAccountDeletedMessage = stringResource(R.string.message_shopkeeper_deleted)
    LaunchedEffect(isMyAccountDeleted) {
      if (isMyAccountDeleted) {
        snackbarHostState.showSnackbar(
          message = myAccountDeletedMessage,
          actionLabel = "dismiss",
          duration = Indefinite,
        )

        appState.loginRepository.setIsMyAccountDeleted(false)
      }
    }

    val shopDeletedMessage = stringResource(R.string.message_shop_deleted)
    LaunchedEffect(isShopDeleted) {
      if (isShopDeleted) {
        snackbarHostState.showSnackbar(
          message = shopDeletedMessage,
          actionLabel = "dismiss",
          duration = Indefinite,
        )

        appState.loginRepository.setIsShopDeleted(false)
      }
    }

    Scaffold(
      modifier = Modifier.semantics {
        testTagsAsResourceId = true
      },
      containerColor = Color.Transparent,
      contentColor = MaterialTheme.colorScheme.onBackground,
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
      snackbarHost = { SnackbarHost(snackbarHostState) },
      bottomBar = {
        val shouldShowBottomBar by appState.shouldShowBottomBar.collectAsStateWithLifecycle()
        if (shouldShowBottomBar) {
          NatBottomBar(
            destinations = appState.topLevelDestinations,
            onNavigateToDestination = appState::navigateToTopLevelDestination,
            currentDestination = appState.currentDestination,
            modifier = Modifier.testTag("NatBottomBar"),
          )
        }
      },
    ) { padding ->
      Row(
        Modifier
          .fillMaxSize()
          .padding(padding)
          .consumeWindowInsets(padding)
          .windowInsetsPadding(
            WindowInsets.safeDrawing.only(
              WindowInsetsSides.Horizontal,
            ),
          ),
      ) {
        Column(Modifier.fillMaxSize()) {
          NatNavHost(
            appState = appState,
            onShowSnackbar = { message, action, duration ->
              snackbarHostState.showSnackbar(
                message = message,
                actionLabel = action,
                duration = duration ?: Short,
              ) == ActionPerformed
            },
          )
        }
      }
    }
  }
}

@Composable
private fun NatBottomBar(
  destinations: List<TopLevelDestination>,
  onNavigateToDestination: (TopLevelDestination) -> Unit,
  currentDestination: NavDestination?,
  modifier: Modifier = Modifier,
) {
  NatNavigationBar(
    modifier = modifier,
  ) {
    destinations.forEach { destination ->
      val selected = currentDestination.isRouteInHierarchy(destination.baseRoute)

      NatNavigationBarItem(
        selected = selected,
        onClick = { onNavigateToDestination(destination) },
        icon = {
          Icon(
            imageVector = destination.unselectedIcon,
            contentDescription = null,
          )
        },
        selectedIcon = {
          Icon(
            imageVector = destination.selectedIcon,
            contentDescription = null,
          )
        },
        label = { Text(stringResource(destination.iconTextId)) },
      )
    }
  }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
  this?.hierarchy?.any {
    it.hasRoute(route)
  } ?: false