package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestShopRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.util.MainDispatcherRule
import com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.navigation.ShopBasicSettingsRoute
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
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
 * `SavedStateHandle.toRoute` which has a dependency on `android.os.Bundle`.
 *
 * TODO: Remove Robolectric if/when AndroidX Navigation API is updated to remove Android dependency.
 *  *  See b/340966212.
 */
@RunWith(RobolectricTestRunner::class)
class ShopBasicSettingsViewModelTest {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val shopRepository = TestShopRepository()

  private lateinit var viewModel: ShopBasicSettingsViewModel

  @Before
  fun setUp() {
    viewModel = ShopBasicSettingsViewModel(
      savedStateHandle = SavedStateHandle(
        route = ShopBasicSettingsRoute(id = testInputShop.datum!!.id!!),
      ),
      shopRepository = shopRepository,
    )
  }

  @Test
  fun stateIsInitiallyLoading() = runTest {
    assertTrue(viewModel.uiState.value.isLoading)
  }

  @Test
  fun stateShop_whenSuccess_matchesShopFromRepository() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    shopRepository.sendShop(testInputShop)

    viewModel.reload()
    val uiStateValue = viewModel.uiState.value
    assertTrue(uiStateValue.success)
    assertFalse(uiStateValue.isLoading)

    val shopFromRepository = shopRepository.getShop(testInputShop.datum!!.id!!).first()

    assertEquals(shopFromRepository, uiStateValue.shop)
  }

  @Test
  fun stateIsUpdated_whenUpdatingShop_becomesTrue() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    shopRepository.sendShop(testInputShop)

    viewModel.reload()

    viewModel.updateName(testInputNewName)
    viewModel.updateDescription(testInputNewDescription)
    viewModel.updateTimeZone(testInputNewTimeZone)

    val uiStateValue = viewModel.uiState.value
    assertEquals(uiStateValue.name, testInputNewName)
    assertEquals(uiStateValue.description, testInputNewDescription)
    assertEquals(uiStateValue.timeZone, testInputNewTimeZone)
    assertFalse(viewModel.hasInvalidData())

    viewModel.updateShop()

    assertTrue(viewModel.uiState.value.isUpdated)
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

  @Test
  fun updating_withoutChanges_isInvalid() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    shopRepository.sendShop(testInputShop)

    viewModel.reload()

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
private const val testInputNewName = "Olivia Clark"
private const val testInputNewDescription = "olivia@example.com"
private const val testInputNewTimeZone = "Hawaii"
