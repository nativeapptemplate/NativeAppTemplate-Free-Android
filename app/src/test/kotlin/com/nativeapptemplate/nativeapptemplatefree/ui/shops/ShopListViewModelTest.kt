package com.nativeapptemplate.nativeapptemplatefree.ui.shops

import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.Shops
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestLoginRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestShopRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ShopListViewModelTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val loginRepository = TestLoginRepository()
  private val shopRepository = TestShopRepository()

  private lateinit var viewModel: ShopListViewModel

  @Before
  fun setUp() {
    viewModel = ShopListViewModel(
      loginRepository = loginRepository,
      shopRepository = shopRepository,
    )
  }

  @Test
  fun stateIsInitiallyLoading() = runTest {
    assertTrue(viewModel.uiState.value.isLoading)
  }

  @Test
  fun stateShops_whenSuccess_matchesShopsFromRepository() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    shopRepository.sendShops(testInputShops)

    viewModel.reload()
    val uiStateValue = viewModel.uiState.value
    assertTrue(uiStateValue.success)
    assertFalse(uiStateValue.isLoading)

    val shopsFromRepository = shopRepository.getShops().first()

    assertEquals(shopsFromRepository, uiStateValue.shops)
  }

  @Test
  fun reload_replacesThePreviousCollectionInsteadOfAddingOne() = runTest {
    viewModel.reload()
    val afterFirstReload = loginRepository.liveSubscriberCount
    org.junit.Assert.assertTrue("the test must observe the live login flows", afterFirstReload > 0)

    viewModel.reload()
    viewModel.reload()

    // Leaked collectors re-emit stale snapshots on every DataStore write.
    org.junit.Assert.assertEquals(afterFirstReload, loginRepository.liveSubscriberCount)
  }

  @Test
  fun recomposing_doesNotStartNewCollectionsOfTheLoginFlows() {
    shopRepository.sendShops(testInputShops)
    loginRepository.sendIsLoggedIn(true)
    // The outer screen reads isLoggedIn during composition.
    composeTestRule.setContent {
      ShopListView(viewModel = viewModel, onItemClick = {}, onAddShopClick = {}, onShowSnackbar = { _, _, _ -> true })
    }
    composeTestRule.runOnIdle { viewModel.reload() }
    composeTestRule.waitForIdle()
    val afterFirstLoad = loginRepository.liveSubscriberCount

    // Each reload changes uiState and recomposes the screen.
    composeTestRule.runOnIdle { viewModel.reload() }
    composeTestRule.waitForIdle()
    composeTestRule.runOnIdle { viewModel.reload() }
    composeTestRule.waitForIdle()

    // A Flow-returning function called in composition starts a new stateIn on every recomposition.
    assertEquals(afterFirstLoad, loginRepository.liveSubscriberCount)
  }
}

private const val SHOP_TYPE = "shop"
private const val SHOP_1_ID = "5712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val SHOP_2_ID = "5712F2DF-DFC7-A3AA-66BC-191203654A1B"
private const val SHOP_3_ID = "5712F2DF-DFC7-A3AA-66BC-191203654A1C"
private const val SHOP_1_NAME = "8th & Townsend"
private const val SHOP_2_NAME = "Kansas & 16th St"
private const val SHOP_3_NAME = "Safeway San Francisco 1490"
private const val SHOP_DESCRIPTION = "This is a shop."
private const val SHOP_TIME_ZONE = "Pacific Time (US & Canada)"

private val testInputShopsData = listOf(
  Data(
    id = SHOP_1_ID,
    type = SHOP_TYPE,
    attributes = Attributes(
      name = SHOP_1_NAME,
      description = SHOP_DESCRIPTION,
      timeZone = SHOP_TIME_ZONE,
    ),
  ),
  Data(
    id = SHOP_2_ID,
    type = SHOP_TYPE,
    attributes = Attributes(
      name = SHOP_2_NAME,
      description = SHOP_DESCRIPTION,
      timeZone = SHOP_TIME_ZONE,
    ),
  ),
  Data(
    id = SHOP_3_ID,
    type = SHOP_TYPE,
    attributes = Attributes(
      name = SHOP_3_NAME,
      description = SHOP_DESCRIPTION,
      timeZone = SHOP_TIME_ZONE,
    ),
  ),
)

private val testInputShops = Shops(
  datum = testInputShopsData,
)
