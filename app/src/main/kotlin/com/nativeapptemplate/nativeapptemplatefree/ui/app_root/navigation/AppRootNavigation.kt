package com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.AcceptPrivacyView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.AcceptTermsView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.ForgotPasswordView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.NeedAppUpdatesView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.OnboardingView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.ResendConfirmationInstructionsView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.SignInEmailAndPasswordView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.SignUpOrSignInView
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.SignUpView
import kotlinx.serialization.Serializable

@Serializable data object OnboardingRoute
@Serializable data object SignUpOrSignInRoute
@Serializable data object SignUpRoute
@Serializable data object SignInEmailAndPasswordRoute
@Serializable data object ForgotPasswordRoute
@Serializable data object ResendConfirmationInstructionsRoute
@Serializable data object NeedAppUpdatesRoute
@Serializable data object AcceptPrivacyRoute
@Serializable data object AcceptTermsRoute

fun NavController.navigateToOnboarding(navOptions: NavOptions? = null) = navigate(route = OnboardingRoute, navOptions)

fun NavGraphBuilder.onboardingView(
  onStartClick: () -> Unit
) {
  composable<OnboardingRoute> {
    OnboardingView(onStartClick)
  }
}

fun NavController.navigateToSignUpOrSignIn() = navigate(SignUpOrSignInRoute)

fun NavGraphBuilder.signUpOrSignInView(
  onSignUpClick: () -> Unit,
  onSignInClick: () -> Unit,
  onBackClick: () -> Unit,
) {
  composable<SignUpOrSignInRoute> {
    SignUpOrSignInView(
      onSignUpClick = onSignUpClick,
      onSignInClick = onSignInClick,
      onBackClick = onBackClick,
    )
  }
}

fun NavController.navigateToSignUp() = navigate(SignUpRoute)

fun NavGraphBuilder.signUpView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable<SignUpRoute> {
    SignUpView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavController.navigateToSignInEmailAndPassword() = navigate(SignInEmailAndPasswordRoute)

fun NavGraphBuilder.signInEmailAndPasswordView(
  onShowForgotPasswordClick: () -> Unit,
  onShowResendConfirmationInstructionsClick: () -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable<SignInEmailAndPasswordRoute> {
    SignInEmailAndPasswordView(
      onShowForgotPasswordClick = onShowForgotPasswordClick,
      onShowResendConfirmationInstructionsClick = onShowResendConfirmationInstructionsClick,
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavController.navigateToForgotPassword() = navigate(ForgotPasswordRoute)

fun NavGraphBuilder.forgotPasswordView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable<ForgotPasswordRoute> {
    ForgotPasswordView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavController.navigateToResendConfirmationInstructions() = navigate(ResendConfirmationInstructionsRoute)

fun NavGraphBuilder.resendConfirmationInstructionsView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable<ResendConfirmationInstructionsRoute> {
    ResendConfirmationInstructionsView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavController.navigateToNeedAppUpdates() = navigate(NeedAppUpdatesRoute)

fun NavGraphBuilder.needAppUpdatesView(
) {
  composable<NeedAppUpdatesRoute> {
    NeedAppUpdatesView(
    )
  }
}

fun NavController.navigateToAcceptPrivacy() = navigate(AcceptPrivacyRoute)

fun NavGraphBuilder.acceptPrivacyView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
) {
  composable<AcceptPrivacyRoute> {
    AcceptPrivacyView(
      onShowSnackbar = onShowSnackbar,
    )
  }
}

fun NavController.navigateToAcceptTerms() = navigate(AcceptTermsRoute)

fun NavGraphBuilder.acceptTermsView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
) {
  composable<AcceptTermsRoute> {
    AcceptTermsView(
      onShowSnackbar = onShowSnackbar,
    )
  }
}
