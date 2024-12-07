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

const val ONBOARDING_ROUTE = "onboarding_route"
const val SIGN_UP_OR_SIGN_IN_ROUTE = "sign_up_or_sign_in_route"
const val SIGN_UP_ROUTE = "sign_up_route"
const val SIGN_IN_EMAIL_AND_PASSWORD_ROUTE = "sign_in_email_and_password_route"
const val FORGOT_PASSWORD_ROUTE = "forgot_password_route"
const val RESEND_CONFIRMATION_INSTRUCTIONS_ROUTE = "resend_confirmation_instructions_route"
const val NEED_APP_UPDATES_ROUTE = "need_app_updates_route"
const val ACCEPT_PRIVACY_ROUTE = "accept_privacy_route"
const val ACCEPT_TERMS_ROUTE = "accept_terms_route"

fun NavController.navigateToOnboarding(navOptions: NavOptions? = null) = navigate(ONBOARDING_ROUTE, navOptions)
fun NavController.navigateToSignUpOrSignIn() = navigate(SIGN_UP_OR_SIGN_IN_ROUTE)
fun NavController.navigateToSignUp() = navigate(SIGN_UP_ROUTE)
fun NavController.navigateToSignInEmailAndPassword() = navigate(SIGN_IN_EMAIL_AND_PASSWORD_ROUTE)
fun NavController.navigateToForgotPassword() = navigate(FORGOT_PASSWORD_ROUTE)
fun NavController.navigateToResendConfirmationInstructions() = navigate(RESEND_CONFIRMATION_INSTRUCTIONS_ROUTE)
fun NavController.navigateToNeedAppUpdatesView() = navigate(NEED_APP_UPDATES_ROUTE)
fun NavController.navigateToAcceptPrivacy() = navigate(ACCEPT_PRIVACY_ROUTE)
fun NavController.navigateToAcceptTerms() = navigate(ACCEPT_TERMS_ROUTE)

fun NavGraphBuilder.onboardingView(
  onStartClick: () -> Unit
) {
  composable(route = ONBOARDING_ROUTE) {
    OnboardingView(onStartClick)
  }
}

fun NavGraphBuilder.signUpOrSignInView(
  onSignUpClick: () -> Unit,
  onSignInClick: () -> Unit,
  onBackClick: () -> Unit,
) {
  composable(route = SIGN_UP_OR_SIGN_IN_ROUTE) {
    SignUpOrSignInView(
      onSignUpClick = onSignUpClick,
      onSignInClick = onSignInClick,
      onBackClick = onBackClick,
    )
  }
}

fun NavGraphBuilder.signUpView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable(route = SIGN_UP_ROUTE) {
    SignUpView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavGraphBuilder.signInEmailAndPasswordView(
  onShowForgotPasswordClick: () -> Unit,
  onShowResendConfirmationInstructionsClick: () -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable(route = SIGN_IN_EMAIL_AND_PASSWORD_ROUTE) {
    SignInEmailAndPasswordView(
      onShowForgotPasswordClick = onShowForgotPasswordClick,
      onShowResendConfirmationInstructionsClick = onShowResendConfirmationInstructionsClick,
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavGraphBuilder.forgotPasswordView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable(route = FORGOT_PASSWORD_ROUTE) {
    ForgotPasswordView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavGraphBuilder.resendConfirmationInstructionsView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  composable(route = RESEND_CONFIRMATION_INSTRUCTIONS_ROUTE) {
    ResendConfirmationInstructionsView(
      onShowSnackbar = onShowSnackbar,
      onBackClick = onBackClick,
    )
  }
}

fun NavGraphBuilder.needAppUpdatesView(
) {
  composable(route = NEED_APP_UPDATES_ROUTE) {
    NeedAppUpdatesView(
    )
  }
}

fun NavGraphBuilder.acceptPrivacyView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
) {
  composable(route = ACCEPT_PRIVACY_ROUTE) {
    AcceptPrivacyView(
      onShowSnackbar = onShowSnackbar,
    )
  }
}

fun NavGraphBuilder.acceptTermsView(
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
) {
  composable(route = ACCEPT_TERMS_ROUTE) {
    AcceptTermsView(
      onShowSnackbar = onShowSnackbar,
    )
  }
}
