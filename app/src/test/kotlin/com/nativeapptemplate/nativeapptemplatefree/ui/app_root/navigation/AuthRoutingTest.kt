package com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.navigation.ShopBaseRoute
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.navigation.ShopsRoute
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AuthRoutingTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  private lateinit var navController: NavHostController
  private val destination: MutableState<AuthDestination?> = mutableStateOf(null)

  // The app's real start destination is ShopBaseRoute, so the back stack starts as [ShopsRoute].
  private val content: @androidx.compose.runtime.Composable () -> Unit = {
    navController = rememberNavController()
    NavHost(navController = navController, startDestination = ShopBaseRoute) {
      navigation<ShopBaseRoute>(startDestination = ShopsRoute) {
        composable<ShopsRoute> {}
      }
      composable<OnboardingRoute> {}
      composable<SignUpOrSignInRoute> {}
      composable<NeedAppUpdatesRoute> {}
      composable<AcceptPrivacyRoute> {}
      composable<AcceptTermsRoute> {}
    }
    AuthRoutingEffect(navController = navController, destination = destination.value)
  }

  /** Screens on the back stack (navigation graphs excluded), by route class simple name. */
  private fun screens(): List<String> = navController.currentBackStack.value
    .filter { it.destination !is NavGraph }
    .map { it.destination.route!!.substringAfterLast('.') }

  private fun route(newDestination: AuthDestination?) {
    destination.value = newDestination
    composeTestRule.waitForIdle()
  }

  @Test
  fun signedOut_onboardingIsTheOnlyScreen_soBackDoesNotRevealShops() {
    composeTestRule.setContent(content)

    route(AuthDestination.ONBOARDING)

    assertEquals(listOf("OnboardingRoute"), screens())
  }

  @Test
  fun signingIn_clearsTheSignedOutScreens() {
    composeTestRule.setContent(content)
    route(AuthDestination.ONBOARDING)
    composeTestRule.runOnIdle { navController.navigate(SignUpOrSignInRoute) }

    route(AuthDestination.SHOPS)

    assertEquals(listOf("ShopsRoute"), screens())
  }

  @Test
  fun signingOut_clearsTheSignedInScreens() {
    composeTestRule.setContent(content)
    route(AuthDestination.SHOPS)

    route(AuthDestination.ONBOARDING)

    assertEquals(listOf("OnboardingRoute"), screens())
  }

  @Test
  fun requiredUpdate_replacesTheStackWithTheUpdateScreen() {
    composeTestRule.setContent(content)
    route(AuthDestination.SHOPS)

    route(AuthDestination.ACCEPT_TERMS)

    assertEquals(listOf("AcceptTermsRoute"), screens())
  }

  @Test
  fun destinationNotLoadedYet_doesNotNavigate() {
    composeTestRule.setContent(content)

    route(null)

    assertEquals(listOf("ShopsRoute"), screens())
  }

  @Test
  fun recreation_withUnchangedDestination_keepsTheScreenTheUserIsOn() {
    val restorationTester = StateRestorationTester(composeTestRule)
    restorationTester.setContent(content)
    route(AuthDestination.ONBOARDING)
    composeTestRule.runOnIdle { navController.navigate(SignUpOrSignInRoute) }

    // Simulates an Activity recreation (e.g. rotation): saved state is restored into a new composition.
    restorationTester.emulateSavedInstanceStateRestore()
    composeTestRule.waitForIdle()

    assertEquals(listOf("OnboardingRoute", "SignUpOrSignInRoute"), screens())
  }

  @Test
  fun authDestination_prioritizesSignInThenAppUpdateThenPrivacyThenTerms() {
    assertEquals(AuthDestination.ONBOARDING, authDestination(isLoggedIn = false, shouldUpdateApp = true, shouldUpdatePrivacy = true, shouldUpdateTerms = true))
    assertEquals(AuthDestination.NEED_APP_UPDATES, authDestination(isLoggedIn = true, shouldUpdateApp = true, shouldUpdatePrivacy = true, shouldUpdateTerms = true))
    assertEquals(AuthDestination.ACCEPT_PRIVACY, authDestination(isLoggedIn = true, shouldUpdateApp = false, shouldUpdatePrivacy = true, shouldUpdateTerms = true))
    assertEquals(AuthDestination.ACCEPT_TERMS, authDestination(isLoggedIn = true, shouldUpdateApp = false, shouldUpdatePrivacy = false, shouldUpdateTerms = true))
    assertEquals(AuthDestination.SHOPS, authDestination(isLoggedIn = true, shouldUpdateApp = false, shouldUpdatePrivacy = false, shouldUpdateTerms = false))
  }
}
