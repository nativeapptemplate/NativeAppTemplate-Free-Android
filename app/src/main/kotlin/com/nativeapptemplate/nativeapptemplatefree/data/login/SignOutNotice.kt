package com.nativeapptemplate.nativeapptemplatefree.data.login

/**
 * Why a sign-out happened, when the signed-out screen should tell the user: stored in the same
 * write that clears the session (see [LoginRepository.clearSession]).
 *
 * Clearing the session routes to onboarding, which removes the screen that asked for the sign-out
 * and cancels its view model's scope; a flag that screen set afterwards could be lost.
 */
enum class SignOutNotice {
  /** The email changed; the new address has to be confirmed before signing in again. */
  EMAIL_UPDATED,

  /** The shopkeeper deleted their account. */
  ACCOUNT_DELETED,
}
