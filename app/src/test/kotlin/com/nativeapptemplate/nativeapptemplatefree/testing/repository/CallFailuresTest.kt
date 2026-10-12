package com.nativeapptemplate.nativeapptemplatefree.testing.repository

import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.util.testApiError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/** The error injection the ViewModel failure tests rely on: a test that cannot fail a call verifies nothing. */
class CallFailuresTest {
  private val shopRepository = TestShopRepository()

  @Test
  fun aFailedCall_throwsTheErrorWhenCollected() = runTest {
    shopRepository.fail(ShopRepository::deleteShop, testApiError)

    // Like the real repositories, the call itself returns a flow; collecting it throws.
    val flow = shopRepository.deleteShop("shop-1")
    val thrown = runCatching { flow.first() }.exceptionOrNull()

    assertSame(testApiError, thrown)
  }

  @Test
  fun otherCalls_stillSucceed() = runTest {
    shopRepository.fail(ShopRepository::createShop, testApiError)

    assertTrue(shopRepository.deleteShop("shop-1").first())
  }
}
