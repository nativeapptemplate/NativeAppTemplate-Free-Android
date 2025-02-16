package com.nativeapptemplate.nativeapptemplatefree.ui.shops

import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestShopRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 *
 * These tests use Robolectric because the subject under test (the ViewModel) uses
 * `TimeZone.getDefault` which has a dependency on `android.icu.impl`.
 */
@RunWith(RobolectricTestRunner::class)
class ShopCreateViewModelTest {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val shopRepository = TestShopRepository()

  private lateinit var viewModel: ShopCreateViewModel

  @Before
  fun setUp() {
    viewModel = ShopCreateViewModel(
      shopRepository = shopRepository,
    )
  }

  @Test
  fun stateIsInitiallyNotLoading() = runTest {
    assertFalse(viewModel.uiState.value.isLoading)
  }

  @Test
  fun stateIsCreated_whenCreatingShop_becomesTrue() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    val name = testInputShop.getName()
    val description = testInputShop.getDescription()
    val timeZone = testInputShop.getTimeZone()

    viewModel.updateName(name)
    viewModel.updateDescription(description)
    viewModel.updateTimeZone(timeZone)

    assertEquals(viewModel.uiState.value.name, name)
    assertEquals(viewModel.uiState.value.description, description)
    assertEquals(viewModel.uiState.value.timeZone, timeZone)
    assertFalse(viewModel.hasInvalidData())

    shopRepository.sendShop(testInputShop)
    viewModel.createShop()

    assertTrue(viewModel.uiState.value.isCreated)
    assertFalse(viewModel.uiState.value.isLoading)
  }

  @Test
  fun blankName_isInvalid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    viewModel.updateName("")
    viewModel.updateDescription(testInputShop.getDescription())
    viewModel.updateTimeZone(testInputShop.getTimeZone())

    assertTrue(viewModel.hasInvalidData())
  }
}

private const val SHOP_TYPE = "shop"
private const val SHOP_ID = "5712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val SHOP_NAME = "8th & Townsend"
private const val SHOP_DESCRIPTION = "This is a shop."
private const val SHOP_TIME_ZONE = "Pacific Time (US & Canada)"

private val testInputShopsData =
  Data(
    id = SHOP_ID,
    type = SHOP_TYPE,
    attributes = Attributes(
      name = SHOP_NAME,
      description = SHOP_DESCRIPTION,
      timeZone = SHOP_TIME_ZONE,
    )
  )

private val testInputShop = Shop(
  datum = testInputShopsData,
)
