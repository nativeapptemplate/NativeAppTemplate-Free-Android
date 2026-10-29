package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import androidx.annotation.StringRes
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
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.designsystem.component.NativeAppTemplateBackground
import com.nativeapptemplate.nativeapptemplatefree.designsystem.component.NativeAppTemplateNavigationBar
import com.nativeapptemplate.nativeapptemplatefree.designsystem.component.NativeAppTemplateNavigationBarItem
import com.nativeapptemplate.nativeapptemplatefree.navigation.NativeAppTemplateNavHost
import com.nativeapptemplate.nativeapptemplatefree.navigation.TopLevelDestination
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

@OptIn(
  ExperimentalComposeUiApi::class,
)
@Composable
fun NativeAppTemplateApp(appState: NativeAppTemplateAppState) {
  NativeAppTemplateBackground {
    val snackbarHostState = remember { SnackbarHostState() }

    val isOffline by appState.isOffline.collectAsStateWithLifecycle()

    val dismissLabel = stringResource(R.string.dismiss)

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

    // Notices stored in DataStore by the screen that caused them (see StoredNotice.kt).
    val resources = LocalResources.current
    LaunchedEffect(appState) {
      val loginRepository = appState.loginRepository
      fun showNotice(@StringRes messageId: Int): suspend () -> Unit = {
        snackbarHostState.showSnackbar(
          message = resources.getString(messageId),
          actionLabel = dismissLabel,
          duration = storedNoticeDuration(messageId),
        )
      }

      // Stored when the shopkeeper is signed out; the app goes on in the same process.
      launch {
        showStoredNotice(
          flag = loginRepository.isEmailUpdated(),
          clear = { loginRepository.setIsEmailUpdated(false) },
          show = showNotice(R.string.reconfirm_description),
        )
      }
      launch {
        showStoredNotice(
          flag = loginRepository.isMyAccountDeleted(),
          clear = { loginRepository.setIsMyAccountDeleted(false) },
          show = showNotice(R.string.message_shopkeeper_deleted),
        )
      }
    }

    // Notices from screens that close once done (MessageBus), one after another
    LaunchedEffect(appState) {
      appState.messageBus.messages.collect { message ->
        snackbarHostState.showSnackbar(
          message = resources.getString(message.textId),
          actionLabel = dismissLabel,
          duration = Short,
        )
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
          NativeAppTemplateBottomBar(
            destinations = appState.topLevelDestinations,
            onNavigateToDestination = appState::navigateToTopLevelDestination,
            currentDestination = appState.currentDestination,
            modifier = Modifier.testTag("NativeAppTemplateBottomBar"),
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
          NativeAppTemplateNavHost(
            appState = appState,
            onShowSnackbar = { message, action, duration ->
              snackbarHostState.showScreenMessage(
                isOffline = isOffline,
                message = message,
                actionLabel = action,
                duration = duration ?: Short,
              )
            },
          )
        }
      }
    }
  }
}

@Composable
private fun NativeAppTemplateBottomBar(
  destinations: List<TopLevelDestination>,
  onNavigateToDestination: (TopLevelDestination) -> Unit,
  currentDestination: NavDestination?,
  modifier: Modifier = Modifier,
) {
  NativeAppTemplateNavigationBar(
    modifier = modifier,
  ) {
    destinations.forEach { destination ->
      val selected = currentDestination.isRouteInHierarchy(destination.baseRoute)

      NativeAppTemplateNavigationBarItem(
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

/**
 * Shows a screen's message, unless offline: a request that fails while offline is the lost
 * connection the offline snackbar already tells about, and queued behind that snackbar, its
 * message came up stale once the connection was back.
 */
internal suspend fun SnackbarHostState.showScreenMessage(
  isOffline: Boolean,
  message: String,
  actionLabel: String?,
  duration: SnackbarDuration,
): Boolean {
  if (isOffline) return false
  return showSnackbar(
    message = message,
    actionLabel = actionLabel,
    duration = duration,
  ) == ActionPerformed
}
