package com.nativeapptemplate.nativeapptemplatefree.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Decodes JSON:API responses as the API sends them: snake_case attributes, related resources in
 * "included", and pagination in "meta". The attribute names are the ones the iOS template's entity
 * adapters read (NativeAppTemplate/Networking/Adapters/EntityAdapters/<Name>Adapter.swift).
 */
class ModelJsonTest {
  // NetModule's networkJson settings for decoding, which the app's Retrofit converter uses.
  private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
  }

  @Test
  fun shop_readsItsAttributes() {
    val shop = json.decodeFromString<Shop>(
      """
      {
        "data": {
          "id": "shop-1",
          "type": "shop",
          "attributes": {
            "name": "Fox Plaza",
            "description": "Ramen",
            "time_zone": "Tokyo",
            "item_tags_count": 9,
            "completed_item_tags_count": 7
          }
        }
      }
      """,
    )

    assertEquals("Fox Plaza", shop.getName())
    assertEquals("Ramen", shop.getDescription())
    assertEquals("Tokyo", shop.getTimeZone())
    assertEquals(7, shop.getCompletedItemTagsCount())
  }

  // A shop without the optional attributes falls back to the defaults (iOS ShopAdapter: "" and 0).
  @Test
  fun shop_withoutOptionalAttributes_usesTheDefaults() {
    val shop = json.decodeFromString<Shop>("""{"data": {"id": "shop-1", "type": "shop", "attributes": {"name": "Fox Plaza"}}}""")

    assertEquals("", shop.getDescription())
    assertEquals(TimeZones.DEFAULT_TIME_ZONE, shop.getTimeZone())
    assertEquals(0, shop.getCompletedItemTagsCount())
  }

  @Test
  fun itemTags_readThePaginationMeta() {
    val itemTags = json.decodeFromString<ItemTags>(
      """
      {
        "data": [
          {"id": "tag-1", "type": "item_tag", "attributes": {"name": "A001", "state": "idled", "shop_id": "shop-1"}},
          {"id": "tag-2", "type": "item_tag", "attributes": {"name": "A002", "state": "completed", "shop_id": "shop-1"}}
        ],
        "meta": {"current_page": 2, "total_pages": 5, "total_count": 42, "limit": 10}
      }
      """,
    )

    assertEquals(listOf("tag-1", "tag-2"), itemTags.datum.map { it.id })
    assertEquals(2, itemTags.meta!!.currentPage)
    assertEquals(5, itemTags.meta!!.totalPages)
    assertEquals(42, itemTags.meta!!.totalCount)
    assertEquals(ItemTagState.Idled, itemTags.datum[0].getItemTagState())
    assertEquals(ItemTagState.Completed, itemTags.datum[1].getItemTagState())
  }

  @Test
  fun itemTag_readsItsAttributes() {
    val itemTag = json.decodeFromString<ItemTag>(
      """
      {
        "data": {
          "id": "tag-1",
          "type": "item_tag",
          "attributes": {
            "shop_id": "shop-1",
            "shop_name": "Fox Plaza",
            "name": "A001",
            "description": "Window seat",
            "position": 3,
            "state": "completed",
            "created_at": "2025-05-01T10:00:00.000+09:00",
            "completed_at": "2025-05-01T10:05:00.000+09:00"
          }
        }
      }
      """,
    )

    assertEquals("tag-1", itemTag.getId())
    assertEquals("A001", itemTag.getName())
    assertEquals("Window seat", itemTag.getDescription())
    assertEquals(3, itemTag.getPosition())
    assertEquals("completed", itemTag.getState())
    assertEquals("shop-1", itemTag.getShopId())
    assertEquals("Fox Plaza", itemTag.getShopName())
    assertEquals("2025-05-01T10:05:00.000+09:00", itemTag.getCompletedAt())
  }

  @Test
  fun loggedInShopkeeper_readsTheSessionAttributes() {
    val shopkeeper = json.decodeFromString<LoggedInShopkeeper>(
      """
      {
        "data": {
          "id": "shopkeeper-1",
          "type": "shopkeeper_sign_in",
          "attributes": {
            "account_id": "account-1",
            "personal_account_id": "account-0",
            "account_owner_id": "owner-1",
            "account_name": "Starbucks",
            "email": "john@example.com",
            "name": "John Smith",
            "time_zone": "Tokyo",
            "locale": "ja",
            "token": "token-1",
            "client": "client-1",
            "uid": "john@example.com",
            "expiry": "1713165114"
          }
        }
      }
      """,
    )

    assertEquals("shopkeeper-1", shopkeeper.getId())
    assertEquals("account-1", shopkeeper.getAccountId())
    assertEquals("account-0", shopkeeper.getPersonalAccountId())
    assertEquals("owner-1", shopkeeper.getAccountOwnerId())
    assertEquals("Starbucks", shopkeeper.getAccountName())
    assertEquals("john@example.com", shopkeeper.getEmail())
    assertEquals("John Smith", shopkeeper.getName())
    assertEquals("Tokyo", shopkeeper.getTimeZone())
    assertEquals("ja", shopkeeper.getLocale())
    assertEquals("token-1", shopkeeper.getToken())
    assertEquals("client-1", shopkeeper.getClient())
    assertEquals("john@example.com", shopkeeper.getUID())
    assertEquals("1713165114", shopkeeper.getExpiry())
  }

  @Test
  fun permissions_readTheMeta() {
    val permissions = json.decodeFromString<Permissions>(
      """
      {
        "data": [
          {"id": "p1", "type": "permission", "attributes": {"name": "update shops", "tag": "update_shops"}},
          {"id": "p2", "type": "permission", "attributes": {"name": "delete shops", "tag": "delete_shops"}}
        ],
        "meta": {
          "android_app_version": 12,
          "should_update_privacy": true,
          "should_update_terms": false,
          "shop_limit_count": 10
        }
      }
      """,
    )

    assertEquals(listOf("p1", "p2"), permissions.datum.map { it.id })
    assertEquals(12, permissions.getAndroidAppVersion())
    assertEquals(true, permissions.getShouldUpdatePrivacy())
    assertEquals(false, permissions.getShouldUpdateTerms())
    assertEquals(10, permissions.getShopLimitCount())
  }

  // A "meta" without a limit leaves it unknown (null), so setPermissions keeps the stored value.
  @Test
  fun permissions_withoutTheLimits_leavesThemUnknown() {
    val permissions = json.decodeFromString<Permissions>("""{"data": [], "meta": {}}""")

    assertNull(permissions.getAndroidAppVersion())
    assertNull(permissions.getShopLimitCount())
  }

  // The shape of the API's error responses (read by ApiResponseExtensions).
  @Test
  fun nativeAppTemplateApiError_readsTheCodeAndMessage() {
    val error = json.decodeFromString<NativeAppTemplateApiError>("""{"code": 422, "error_message": "Name has already been taken"}""")

    assertEquals(422, error.code)
    assertEquals("Name has already been taken", error.message)
  }
}
