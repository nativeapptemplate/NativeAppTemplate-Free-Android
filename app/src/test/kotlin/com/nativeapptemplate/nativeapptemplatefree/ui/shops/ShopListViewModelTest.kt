package com.nativeapptemplate.nativeapptemplatefree.ui.shops

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

import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ShopListViewModelTest {
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
