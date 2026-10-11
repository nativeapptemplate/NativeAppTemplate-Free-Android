package com.nativeapptemplate.nativeapptemplatefree.di.modules

import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.SignUp
import com.nativeapptemplate.nativeapptemplatefree.model.SignUpForUpdate
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Request bodies and responses as Retrofit encodes/decodes them ([networkJson], encodeDefaults = false). */
class NetworkJsonLocaleTest {
  @Test
  fun signUp_sendsTheDeviceLanguageTag() {
    val body = SignUp(
      name = "John Smith",
      email = "john@example.com",
      timeZone = "Tokyo",
      password = "password",
      currentPlatform = "android",
      locale = "ja-JP",
    )

    val json = networkJson.parseToJsonElement(networkJson.encodeToString(SignUp.serializer(), body)).jsonObject

    // POST /shopkeeper_auth takes the device's tag; the server reduces it to "ja".
    assertEquals(JsonPrimitive("ja-JP"), json["locale"])
  }

  @Test
  fun signUpForUpdate_sendsTheLocaleEvenWhenItIsTheDefaultLanguage() {
    // "en" is the server's default; a property default of "en" would silently drop it from the body.
    val body = SignUpForUpdate(name = "John Smith", email = "john@example.com", timeZone = "Tokyo", locale = "en")

    val json = networkJson.parseToJsonElement(networkJson.encodeToString(SignUpForUpdate.serializer(), body)).jsonObject

    assertEquals(JsonPrimitive("en"), json["locale"])
  }

  @Test
  fun signUpForUpdate_sendsJapanese() {
    val body = SignUpForUpdate(name = "John Smith", email = "john@example.com", timeZone = "Tokyo", locale = "ja")

    val json = networkJson.parseToJsonElement(networkJson.encodeToString(SignUpForUpdate.serializer(), body)).jsonObject

    assertEquals(JsonPrimitive("ja"), json["locale"])
  }

  @Test
  fun signInResponse_parsesTheLocale() {
    // Shape of the sign-in / sign-up / profile-update response: locale is in data.attributes.
    val response = """
      {"data": {"id": "1", "type": "shopkeeper_sign_in",
        "attributes": {"name": "John Smith", "time_zone": "Tokyo", "locale": "ja"}}}
    """.trimIndent()

    val shopkeeper = networkJson.decodeFromString(LoggedInShopkeeper.serializer(), response)

    assertEquals("ja", shopkeeper.getLocale())
  }

  @Test
  fun responseWithoutALocale_parsesAsNull() {
    // An older server omits it; the data source then keeps the stored (or default) locale.
    val response = """{"data": {"id": "1", "attributes": {"name": "John Smith"}}}"""

    val shopkeeper = networkJson.decodeFromString(LoggedInShopkeeper.serializer(), response)

    assertNull(shopkeeper.getLocale())
  }
}
