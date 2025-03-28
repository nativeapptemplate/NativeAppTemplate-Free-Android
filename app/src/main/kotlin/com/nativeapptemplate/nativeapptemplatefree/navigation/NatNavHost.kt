package com.nativeapptemplate.nativeapptemplatefree.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.navigation.navigateToPasswordEdit
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.navigation.navigateToShopkeeperEdit
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.navigation.passwordEditView
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.navigation.settingsView
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.navigation.shopkeeperEditView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.NatAppState
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.acceptPrivacyView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.acceptTermsView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.forgotPasswordView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.navigateToAcceptPrivacy
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.navigateToAcceptTerms
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.navigateToForgotPassword
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.navigateToNeedAppUpdates
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.navigateToOnboarding
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.navigateToResendConfirmationInstructions
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.navigateToSignInEmailAndPassword
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.navigateToSignUp
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.navigateToSignUpOrSignIn
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.needAppUpdatesView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.onboardingView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.resendConfirmationInstructionsView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.signInEmailAndPasswordView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.signUpOrSignInView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.signUpView
import com.nativeapptemplate.nativeapptemplatefree.ui.scan.navigation.doScanView
import com.nativeapptemplate.nativeapptemplatefree.ui.scan.navigation.navigateToDoScan
import com.nativeapptemplate.nativeapptemplatefree.ui.scan.navigation.scanBaseView
import com.nativeapptemplate.nativeapptemplatefree.ui.scan.navigation.scanView
import com.nativeapptemplate.nativeapptemplatefree.ui.settings.navigation.settingBaseView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail.navigation.navigateToShopDetail
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail.navigation.shopDetailView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.itemTagCreateView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.itemTagDetailView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.itemTagEditView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.itemTagListView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.itemTagWriteView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.navigateToItemTagCreate
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.navigateToItemTagDetail
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.navigateToItemTagEdit
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.navigateToItemTagList
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.navigateToItemTagWrite
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.navigateToNumberTagsWebpageList
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.navigateToShopBasicSettings
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.navigateToShopSettings
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.numberTagsWebpageListView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.shopBasicSettingsView
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.shopSettingsView
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.navigation.ShopBaseRoute
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.navigation.navigateToShopCreate
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.navigation.navigateToShopList
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.navigation.shopBaseView
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.navigation.shopCreateView
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.navigation.shopListView

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun NatNavHost(
  appState: NatAppState,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  modifier: Modifier = Modifier,
) {
  val navController = appState.navController
  val isLoggedIn by appState.isLoggedIn.collectAsStateWithLifecycle()
  val shouldUpdateApp by appState.shouldUpdateApp.collectAsStateWithLifecycle()
  val shouldUpdatePrivacy by appState.shouldUpdatePrivacy.collectAsStateWithLifecycle()
  val shouldUpdateTerms by appState.shouldUpdateTerms.collectAsStateWithLifecycle()
  val shouldNavigateToScanView by appState.shouldNavigateToScanView.collectAsStateWithLifecycle()

 LaunchedEffect(
    isLoggedIn,
    shouldUpdateApp,
    shouldUpdatePrivacy,
    shouldUpdateTerms,
  ) {
    if (isLoggedIn) {
      if (shouldUpdateApp) {
        navController.navigateToNeedAppUpdates()
      } else if (shouldUpdatePrivacy) {
        navController.navigateToAcceptPrivacy()
      } else if (shouldUpdateTerms) {
        navController.navigateToAcceptTerms()
      } else {
        navController.navigateToShopList()
      }
    } else {
      navController.navigateToOnboarding()
    }
  }

  LaunchedEffect(shouldNavigateToScanView) {
    if (shouldNavigateToScanView) {
      appState.navigateToScan()
    }
  }

  NavHost(
    navController = navController,
    startDestination = ShopBaseRoute,
    modifier = modifier,
  ) {
    onboardingView(
      onStartClick = { navController.navigateToSignUpOrSignIn() },
    )
    signUpOrSignInView(
      onSignUpClick = { navController.navigateToSignUp() },
      onSignInClick = { navController.navigateToSignInEmailAndPassword() },
      onBackClick = navController::popBackStack,
    )
    signUpView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = navController::popBackStack,
    )
    signInEmailAndPasswordView(
      onShowForgotPasswordClick = { navController.navigateToForgotPassword() },
      onShowResendConfirmationInstructionsClick = { navController.navigateToResendConfirmationInstructions() },
      onShowSnackbar = onShowSnackbar,
      onBackClick = navController::popBackStack,
    )
    forgotPasswordView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = navController::popBackStack,
    )
    resendConfirmationInstructionsView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = navController::popBackStack,
    )

    needAppUpdatesView()
    acceptPrivacyView (
      onShowSnackbar = onShowSnackbar,
    )
    acceptTermsView (
      onShowSnackbar = onShowSnackbar,
    )

    shopBaseView {
      shopListView(
        onItemClick = { shopId -> navController.navigateToShopDetail(shopId) },
        onAddShopClick = { navController.navigateToShopCreate() },
        onShowSnackbar = onShowSnackbar,
      )
      shopCreateView(
        onShowSnackbar = onShowSnackbar,
        onBackClick = navController::popBackStack,
      )
      shopDetailView(
        onSettingsClick = { shopId -> navController.navigateToShopSettings(shopId) },
        onShowSnackbar = onShowSnackbar,
        onBackClick = navController::popBackStack,
      )
      shopSettingsView(
        onShowBasicSettingsClick = { shopId -> navController.navigateToShopBasicSettings(shopId) },
        onShowItemTagListClick = { shopId -> navController.navigateToItemTagList(shopId) },
        onShowNumberTagsWebpageListClick = { shopId -> navController.navigateToNumberTagsWebpageList(shopId) },

        onShowSnackbar = onShowSnackbar,
        onBackClick = navController::popBackStack,
      )
      shopBasicSettingsView(
        onShowSnackbar = onShowSnackbar,
        onBackClick = navController::popBackStack,
      )

      numberTagsWebpageListView(
        onShowSnackbar = onShowSnackbar,
        onBackClick = navController::popBackStack,
      )

      itemTagListView(
        onItemClick = { itemTagId -> navController.navigateToItemTagDetail(itemTagId) },
        onAddItemTagClick = { shopId -> navController.navigateToItemTagCreate(shopId) },
        onShowSnackbar = onShowSnackbar,
        onBackClick =navController::popBackStack,
      )
      itemTagCreateView(
        onShowSnackbar = onShowSnackbar,
        onBackClick = navController::popBackStack,
      )
      itemTagDetailView(
        onShowItemTagEditClick = { itemTagId -> navController.navigateToItemTagEdit(itemTagId) },
        onShowItemTagWriteClick = { itemTagId, isLock, itemTagType -> navController.navigateToItemTagWrite(itemTagId, isLock, itemTagType) },
        onShowSnackbar = onShowSnackbar,
        onBackClick = navController::popBackStack,
      )
      itemTagEditView(
        onShowSnackbar = onShowSnackbar,
        onBackClick = navController::popBackStack,
      )
      itemTagWriteView(
        onBackClick = navController::popBackStack,
      )
    }

    scanBaseView {
      scanView(
        onShowDoScanViewClick = { isTest -> navController.navigateToDoScan(isTest) },
        onShowSnackbar = onShowSnackbar,
      )
      doScanView(
        onBackClick = navController::popBackStack,
      )
    }

    settingBaseView {
      settingsView(
        onShowShopkeeperEditClick = { navController.navigateToShopkeeperEdit() },
        onShowPasswordEditClick = { navController.navigateToPasswordEdit() },
        onShowSnackbar = onShowSnackbar,
      )
      shopkeeperEditView(
        onShowSnackbar = onShowSnackbar,
        onBackClick =navController::popBackStack,
      )
      passwordEditView(
        onShowSnackbar = onShowSnackbar,
        onBackClick =navController::popBackStack,
      )
    }
  }
}
