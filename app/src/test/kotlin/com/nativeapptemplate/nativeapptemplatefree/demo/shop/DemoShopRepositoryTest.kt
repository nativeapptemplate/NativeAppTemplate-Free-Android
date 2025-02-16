package com.nativeapptemplate.nativeapptemplatefree.demo.shop

import com.nativeapptemplate.nativeapptemplatefree.demo.DemoAssetManagerImpl
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.Meta
import com.nativeapptemplate.nativeapptemplatefree.model.Shop
import com.nativeapptemplate.nativeapptemplatefree.model.ShopBody
import com.nativeapptemplate.nativeapptemplatefree.model.ShopBodyDetail
import com.nativeapptemplate.nativeapptemplatefree.model.ShopUpdateBody
import com.nativeapptemplate.nativeapptemplatefree.model.ShopUpdateBodyDetail
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DemoShopRepositoryTest {
  private lateinit var subject: DemoShopRepository
  private val testDispatcher = StandardTestDispatcher()

  private val shopData = Data(
    id = "5712F2DF-DFC7-A3AA-66BC-191203654A1C",
    type = "shop",
    attributes = Attributes(
      name = "Shop1",
      description = "This is a Shop1",
      timeZone = "Tokyo",
    ),
    meta = Meta(
      limitCount = 96,
      createdShopsCount = 3
    ),
  )

  @Before
  fun setUp() {
    subject = DemoShopRepository(
      ioDispatcher = testDispatcher,
      networkJson = Json { ignoreUnknownKeys = true },
      assets = DemoAssetManagerImpl,
    )
  }

  @Test
  fun testDeserializationOfShops() = runTest(testDispatcher) {
    kotlin.test.assertEquals(
      shopData,
      subject.getShops().first().datum.first(),
    )
  }

  @Test
  fun testDeserializationOfShop() = runTest(testDispatcher) {
    kotlin.test.assertEquals(
      Shop(datum = shopData),
      subject.getShop(id = "5712F2DF-DFC7-A3AA-66BC-191203654A1C").first(),
    )
  }

  @Test
  fun testDeserializationOfShopFromCreating() = runTest(testDispatcher) {
    kotlin.test.assertEquals(
      Shop(datum = shopData),
      subject.createShop(
        ShopBody(
          shopBodyDetail = ShopBodyDetail(
            name = "Shop1",
            description = "This is a Shop1",
            timeZone = "Tokyo",
          )
         )
      ).first(),
    )
  }

  @Test
  fun testDeserializationOfShopFromUpdating() = runTest(testDispatcher) {
    kotlin.test.assertEquals(
      Shop(datum = shopData),
      subject.updateShop(
        id = "5712F2DF-DFC7-A3AA-66BC-191203654A1C",
        shopUpdateBody = ShopUpdateBody(
          shopUpdateBodyDetail = ShopUpdateBodyDetail(
            name = "Shop1",
            description = "This is a Shop1",
            timeZone = "Tokyo",
          ),
        ),
      ).first(),
    )
  }

  @Test
  fun testDeleteOfShop() = runTest(testDispatcher) {
    kotlin.test.assertTrue(subject.deleteShop("5712F2DF-DFC7-A3AA-66BC-191203654A1C").first())
  }
}
