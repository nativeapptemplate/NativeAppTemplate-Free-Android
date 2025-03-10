package com.nativeapptemplate.nativeapptemplatefree.demo.item_tag

import com.nativeapptemplate.nativeapptemplatefree.demo.DemoAssetManagerImpl
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagBody
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagBodyDetail
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class DemoItemTagRepositoryTest {
  private lateinit var subject: DemoItemTagRepository
  private val testDispatcher = StandardTestDispatcher()

    private val itemTagData = Data(
    id = "9712F2DF-DFC7-A3AA-66BC-191203654A1A",
    type = "item_tag",
    attributes = Attributes(
      shopId = "5712F2DF-DFC7-A3AA-66BC-191203654A1A",
      queueNumber = "A001",
      state = "idled",
      scanState = "unscanned",
      createdAt = "2025-01-02T12:00:00.000Z",
      shopName = "8th & Townsend",
      customerReadAt = "2025-01-02T12:00:01.000Z",
      completedAt = "2025-01-02T12:00:03.000Z",
      alreadyCompleted = false
    ),
  )

  @Before
  fun setUp() {
    subject = DemoItemTagRepository(
      ioDispatcher = testDispatcher,
      networkJson = Json { ignoreUnknownKeys = true },
      assets = DemoAssetManagerImpl,
    )
  }

  @Test
  fun testDeserializationOfItemTags() = runTest(testDispatcher) {
    assertEquals(
      itemTagData,
      subject.getItemTags(itemTagData.getShopId()!!).first().datum.first(),
    )
  }

  @Test
  fun testDeserializationOfItemTag() = runTest(testDispatcher) {
    assertEquals(
      ItemTag(datum = itemTagData),
      subject.getItemTag(id = itemTagData.id!!).first(),
    )
  }

  @Test
  fun testDeserializationOfItemTagFromCreating() = runTest(testDispatcher) {
    assertEquals(
      ItemTag(datum = itemTagData),
      subject.createItemTag(
        itemTagData.getShopId()!!,
        ItemTagBody(
          itemTagBodyDetail = ItemTagBodyDetail(
            queueNumber = itemTagData.getQueueNumber()
          )
         )
      ).first(),
    )
  }

  @Test
  fun testDeserializationOfItemTagFromUpdating() = runTest(testDispatcher) {
    assertEquals(
      ItemTag(datum = itemTagData),
      subject.updateItemTag(
        id = itemTagData.id!!,
        itemTagBody = ItemTagBody(
          itemTagBodyDetail = ItemTagBodyDetail(
            queueNumber = itemTagData.getQueueNumber()
          ),
        ),
      ).first(),
    )
  }

  @Test
  fun testDeleteOfItemTag() = runTest(testDispatcher) {
    assertTrue(subject.deleteItemTag(itemTagData.id!!).first())
  }
}
