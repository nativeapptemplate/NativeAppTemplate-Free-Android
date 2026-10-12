package com.nativeapptemplate.nativeapptemplatefree.navigation

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail.navigation.ShopDetailRoute
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail.navigation.navigateToShopDetail
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagDetailRoute
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ItemTagListRoute
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.navigateToItemTagDetail
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.navigateToItemTagList
import com.nativeapptemplate.nativeapptemplatefree.ui.shops.navigation.ShopsRoute
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The navigateTo* helpers NativeAppTemplateNavHost calls put their arguments in the route, and each
 * ViewModel reads them back with SavedStateHandle.toRoute. Each argument must arrive unchanged, even
 * when it holds characters that are special in a route URL.
 */
@RunWith(RobolectricTestRunner::class)
class RouteArgumentsTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  private lateinit var navController: NavHostController

  private fun setNavHost() {
    composeTestRule.setContent {
      navController = rememberNavController()
      NavHost(navController = navController, startDestination = ShopsRoute) {
        composable<ShopsRoute> {}
        composable<ShopDetailRoute> {}
        composable<ItemTagListRoute> {}
        composable<ItemTagDetailRoute> {}
      }
    }
  }

  private fun navigate(navigation: NavHostController.() -> Unit) {
    composeTestRule.runOnIdle { navController.navigation() }
    composeTestRule.waitForIdle()
  }

  private inline fun <reified T : Any> currentRoute(): T = composeTestRule.runOnIdle { navController.currentBackStackEntry!!.toRoute<T>() }

  @Test
  fun shopDetail_keepsTheId() {
    setNavHost()

    navigate { navigateToShopDetail("shop/1?a=b&c#d %") }

    assertEquals(ShopDetailRoute("shop/1?a=b&c#d %"), currentRoute<ShopDetailRoute>())
  }

  @Test
  fun itemTagDetail_keepsTheId() {
    setNavHost()

    navigate { navigateToItemTagDetail("tag/1?x 店舗") }

    assertEquals(ItemTagDetailRoute("tag/1?x 店舗"), currentRoute<ItemTagDetailRoute>())
  }

  // Back from a tag returns to the tag list of the same shop.
  @Test
  fun back_fromATag_returnsToItsShopsTagList() {
    setNavHost()
    navigate { navigateToItemTagList("shop-1") }
    navigate { navigateToItemTagDetail("tag-2") }
    assertEquals(ItemTagDetailRoute("tag-2"), currentRoute<ItemTagDetailRoute>())

    navigate { popBackStack() }

    assertEquals(ItemTagListRoute("shop-1"), currentRoute<ItemTagListRoute>())
  }
}
