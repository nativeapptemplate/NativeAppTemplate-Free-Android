package com.nativeapptemplate.nativeapptemplatefree.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * The app does not merge the resources in a response's "included" into anything: each resource
 * keeps the relationship references it was decoded with (`{"id", "type"}` only), and the list and
 * detail models hand out the decoded `data` as is. (A no-op `Data.updateRelationships` and its
 * `getDatumWithRelationships()` callers used to suggest otherwise; they were removed.)
 */
class DataRelationshipsTest {
  // NetModule's networkJson settings for decoding.
  private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
  }

  // A JSON:API list response: each item tag refers to its shop, whose attributes are in "included".
  private val itemTagsResponse = """
    {
      "data": [
        {
          "id": "item-tag-1",
          "type": "item_tag",
          "attributes": {"name": "A001", "state": "idled"},
          "relationships": {"shop": {"data": {"id": "shop-1", "type": "shop"}}}
        }
      ],
      "included": [
        {"id": "shop-1", "type": "shop", "attributes": {"name": "Fox Plaza", "time_zone": "Tokyo"}}
      ]
    }
  """

  @Test
  fun itemTags_decodedWithIncludedResources_keepOnlyTheRelationshipReference() {
    val itemTags = json.decodeFromString<ItemTags>(itemTagsResponse)
    val itemTag = itemTags.datum.single()

    // The shop reference keeps only what "relationships" carried: the included name is not merged in.
    assertEquals("shop-1", itemTag.relationships?.shop?.datum?.id)
    assertNull(itemTag.relationships?.shop?.datum?.attributes)
    assertEquals("Fox Plaza", itemTags.included?.single()?.getName())
  }

  @Test
  fun itemTag_getData_withIncludedResources_isTheDecodedData() {
    val datum = Data(
      id = "item-tag-1",
      type = "item_tag",
      attributes = Attributes(name = "A001"),
      relationships = Relationships(shop = Shop(datum = Data(id = "shop-1", type = "shop"))),
    )
    val itemTag = ItemTag(datum = datum, included = listOf(Data(id = "shop-1", type = "shop", attributes = Attributes(name = "Fox Plaza"))))

    assertEquals(datum, itemTag.getData())
    // So getShopName() comes from the item tag's own attributes, not from "included".
    assertEquals("", itemTag.getShopName())
  }

  @Test
  fun shop_getData_withIncludedResources_isTheDecodedData() {
    val datum = Data(id = "shop-1", type = "shop", attributes = Attributes(name = "Fox Plaza"), relationships = Relationships(shops = Shops()))
    val shop = Shop(datum = datum, included = listOf(Data(id = "item-tag-1", type = "item_tag")))

    assertEquals(datum, shop.getData())
  }
}
