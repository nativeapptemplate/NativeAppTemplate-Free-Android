package com.nativeapptemplate.nativeapptemplatefree.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

/**
 * Characterizes `Data.updateRelationships` and the `getDatumWithRelationships()` / `getData()`
 * callers as they behave today: the resources in "included" are NOT merged into anything. Each
 * resource keeps the relationship references it was decoded with (`{"id", "type"}` only), so the
 * calls return values equal to the decoded `data`. These tests pin that down so a refactoring
 * (removing the no-op, or making it merge "included") is a deliberate, visible change.
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
  fun updateRelationships_withIncludedResources_returnsAnEqualCopy() {
    val itemTags = json.decodeFromString<ItemTags>(itemTagsResponse)
    val itemTag = itemTags.datum.single()

    val updated = itemTag.updateRelationships(itemTags.included)

    assertEquals(itemTag, updated)
    // The shop reference keeps only what "relationships" carried: the included name is not merged in.
    assertEquals("shop-1", updated.relationships?.shop?.datum?.id)
    assertNull(updated.relationships?.shop?.datum?.attributes)
  }

  @Test
  fun updateRelationships_withoutIncludedResources_returnsTheSameInstance() {
    val itemTag = json.decodeFromString<ItemTags>(itemTagsResponse).datum.single()

    assertSame(itemTag, itemTag.updateRelationships(null))
    assertSame(itemTag, itemTag.updateRelationships(emptyList()))
  }

  @Test
  fun itemTags_getDatumWithRelationships_isTheDecodedData() {
    val itemTags = json.decodeFromString<ItemTags>(itemTagsResponse)

    assertEquals(itemTags.datum, itemTags.getDatumWithRelationships())
  }

  @Test
  fun shops_getDatumWithRelationships_isTheDecodedData() {
    val shops = Shops(
      datum = listOf(Data(id = "shop-1", type = "shop", attributes = Attributes(name = "Fox Plaza"), relationships = Relationships(shops = Shops()))),
      included = listOf(Data(id = "item-tag-1", type = "item_tag", attributes = Attributes(name = "A001"))),
    )

    assertEquals(shops.datum, shops.getDatumWithRelationships())
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
