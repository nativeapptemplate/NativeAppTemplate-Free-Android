package com.nativeapptemplate.nativeapptemplatefree

import android.content.Intent
import android.graphics.Color
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nativeapptemplate.nativeapptemplatefree.MainActivityUiState.Loading
import com.nativeapptemplate.nativeapptemplatefree.MainActivityUiState.Success
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.designsystem.theme.NatTheme
import com.nativeapptemplate.nativeapptemplatefree.model.DarkThemeConfig
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagInfoFromNdefMessage
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.NatApp
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.rememberNatAppState
import com.nativeapptemplate.nativeapptemplatefree.utils.NetworkMonitor
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @Inject
  lateinit var networkMonitor: NetworkMonitor

  @Inject
  lateinit var loginRepository: LoginRepository

  private val viewModel: MainActivityViewModel by viewModels()
  var uiState: MainActivityUiState by mutableStateOf(Loading)

  override fun onCreate(savedInstanceState: Bundle?) {
    val splashScreen = installSplashScreen()
    super.onCreate(savedInstanceState)

    viewModel.updateShouldNavigateToScanView(false)
    viewModel.updateShouldFetchItemTagForShowTagInfoScan(false)
    viewModel.updateShouldCompleteItemTagForCompleteScan(false)
    viewModel.initScanViewSelectedTabIndex()
    viewModel.initShowTagInfoScanResult()
    viewModel.initCompleteScanResult()

//    viewModel.updateDidShowTapShopBelowTip(false)
//    viewModel.updateDidShowReadInstructionsTip(false)

    // Update the uiState
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiState.collect {
          uiState = it
        }
      }
    }

    // Keep the splash screen on-screen until the UI state is loaded. This condition is
    // evaluated each time the app needs to be redrawn so it should be fast to avoid blocking
    // the UI.
    splashScreen.setKeepOnScreenCondition {
      when (uiState) {
        Loading -> true
        is Success -> false
      }
    }

    // Turn off the decor fitting system windows, which allows us to handle insets,
    // including IME animations, and go edge-to-edge
    // This also sets up the initial system bar style based on the platform theme
    enableEdgeToEdge()

    setContent {
      val darkTheme = shouldUseDarkTheme(uiState)

      // Update the edge to edge configuration to match the theme
      // This is the same parameters as the default enableEdgeToEdge call, but we manually
      // resolve whether or not to show dark theme using uiState, since it can be different
      // than the configuration's dark theme value based on the user preference.
      DisposableEffect(darkTheme) {
        enableEdgeToEdge(
          statusBarStyle = SystemBarStyle.auto(
            Color.TRANSPARENT,
            Color.TRANSPARENT,
          ) { darkTheme },
          navigationBarStyle = SystemBarStyle.auto(
            lightScrim,
            darkScrim,
          ) { darkTheme },
        )
        onDispose {}
      }

      val appState = rememberNatAppState(
        loginRepository = loginRepository,
        networkMonitor = networkMonitor,
      )

      NatTheme(
        darkTheme = darkTheme,
      ) {
        NatApp(appState)
      }
    }

    if (!uiState.isLoggedIn) return

    val intent = intent
    if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
      viewModel.updateShouldNavigateToScanView(false)

      val ndefMessage: NdefMessage?
      val rawMessages = if (SDK_INT >= 33) { // TIRAMISU
        intent.getParcelableArrayExtra(
          NfcAdapter.EXTRA_NDEF_MESSAGES,
          NdefMessage::class.java
        )
      }else{
        @Suppress("DEPRECATION")
        intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
      }

      if (!rawMessages.isNullOrEmpty()) {
        ndefMessage = rawMessages[0] as NdefMessage

        val itemTagInfoFromNdefMessage = Utility.extractItemTagInfoFrom(
          context = this,
          ndefMessage = ndefMessage
        )

        updateItemTagInfoFromNdefMessage(itemTagInfoFromNdefMessage)
        viewModel.initScanViewSelectedTabIndex()
        viewModel.updateShouldNavigateToScanView(true)
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)

    if (!uiState.isLoggedIn) return

    if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
      viewModel.updateShouldNavigateToScanView(false)

      val ndefMessage: NdefMessage?
      val rawMessages = if (SDK_INT >= 33) { // TIRAMISU
        intent.getParcelableArrayExtra(
          NfcAdapter.EXTRA_NDEF_MESSAGES,
          NdefMessage::class.java
        )
      }else{
        @Suppress("DEPRECATION")
        intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
      }

      if (!rawMessages.isNullOrEmpty()) {
        ndefMessage = rawMessages[0] as NdefMessage

        val itemTagInfoFromNdefMessage = Utility.extractItemTagInfoFrom(
          context = this,
          ndefMessage = ndefMessage
        )

        updateItemTagInfoFromNdefMessage(itemTagInfoFromNdefMessage)
        viewModel.initScanViewSelectedTabIndex()
        viewModel.updateShouldNavigateToScanView(true)
      }
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.updatePermissions()
  }

  private fun updateItemTagInfoFromNdefMessage(itemTagInfoFromNdefMessage: ItemTagInfoFromNdefMessage) {
    if (itemTagInfoFromNdefMessage.success) {
      itemTagInfoFromNdefMessage.scannedAt = Date().toInstant().toString()
    }

    viewModel.updateItemTagInfoFromNdefMessage(itemTagInfoFromNdefMessage)
    viewModel.updateShouldCompleteItemTagForCompleteScan(itemTagInfoFromNdefMessage.success)
  }
}

/**
 * Returns `true` if dark theme should be used, as a function of the [uiState] and the
 * current system context.
 */
@Composable
private fun shouldUseDarkTheme(
  uiState: MainActivityUiState,
): Boolean = when (uiState) {
  Loading -> isSystemInDarkTheme()
  is Success -> when (uiState.userData.darkThemeConfig) {
    DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
    DarkThemeConfig.LIGHT -> false
    DarkThemeConfig.DARK -> true
  }
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/kotlin/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/kotlin/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
