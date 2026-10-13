package com.nativeapptemplate.nativeapptemplatefree.data

import com.nativeapptemplate.nativeapptemplatefree.UserPreferences
import com.nativeapptemplate.nativeapptemplatefree.common.errors.ApiException
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagApi
import com.nativeapptemplate.nativeapptemplatefree.data.item_tag.ItemTagRepositoryImpl
import com.nativeapptemplate.nativeapptemplatefree.data.login.AccountPasswordApi
import com.nativeapptemplate.nativeapptemplatefree.data.login.AccountPasswordRepositoryImpl
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginApi
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepositoryImpl
import com.nativeapptemplate.nativeapptemplatefree.data.login.SignUpApi
import com.nativeapptemplate.nativeapptemplatefree.data.login.SignUpRepositoryImpl
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopApi
import com.nativeapptemplate.nativeapptemplatefree.data.shop.ShopRepositoryImpl
import com.nativeapptemplate.nativeapptemplatefree.datastore.NativeAppTemplatePreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.datastoreTest.InMemoryDataStore
import com.nativeapptemplate.nativeapptemplatefree.di.modules.NetModule
import com.nativeapptemplate.nativeapptemplatefree.di.modules.sessionRedactingLoggingInterceptor
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagBody
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagBodyDetail
import com.nativeapptemplate.nativeapptemplatefree.model.SendConfirmation
import com.nativeapptemplate.nativeapptemplatefree.model.SendResetPassword
import com.nativeapptemplate.nativeapptemplatefree.model.ShopBody
import com.nativeapptemplate.nativeapptemplatefree.model.ShopBodyDetail
import com.nativeapptemplate.nativeapptemplatefree.model.ShopUpdateBody
import com.nativeapptemplate.nativeapptemplatefree.model.ShopUpdateBodyDetail
import com.nativeapptemplate.nativeapptemplatefree.model.SignUp
import com.nativeapptemplate.nativeapptemplatefree.model.SignUpForUpdate
import com.nativeapptemplate.nativeapptemplatefree.model.UpdatePasswordBody
import com.nativeapptemplate.nativeapptemplatefree.model.UpdatePasswordBodyDetail
import com.nativeapptemplate.nativeapptemplatefree.network.AcceptLanguageInterceptor
import com.nativeapptemplate.nativeapptemplatefree.network.AuthInterceptor
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import java.net.InetAddress
import java.net.InetSocketAddress
import kotlin.test.assertFailsWith

/**
 * The repositories through the app's real network stack (NetModule's OkHttp client and Retrofit
 * converters, AuthInterceptor, AcceptLanguageInterceptor) against a local HTTP server, so the
 * requests the server receives and how its answers come back are pinned down before refactoring.
 *
 * Paths and methods come from the API routes the Retrofit interfaces declare (e.g. ItemTagApi:
 * `GET {account_id}/api/v1/shopkeeper/shops/{shop_id}/item_tags`); the account id is the one saved
 * at sign-in (UserPreferences.accountId).
 */
class RepositoryRequestsTest {
  private class Received(val method: String, val pathAndQuery: String, val headers: Map<String, String?>, val body: String)

  private val received = mutableListOf<Received>()
  private var status = 200
  private var responseBody = "{}"

  private val server = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0).apply {
    createContext("/") { exchange ->
      val uri = exchange.requestURI
      received += Received(
        method = exchange.requestMethod,
        pathAndQuery = uri.rawPath + (uri.rawQuery?.let { "?$it" } ?: ""),
        headers = exchange.requestHeaders.mapKeys { it.key.lowercase() }.mapValues { it.value.firstOrNull() },
        body = exchange.requestBody.readBytes().decodeToString(),
      )
      val bytes = responseBody.encodeToByteArray()
      exchange.responseHeaders.add("Content-Type", "application/json; charset=utf-8")
      exchange.sendResponseHeaders(status, bytes.size.toLong())
      exchange.responseBody.use { it.write(bytes) }
    }
    start()
  }

  @After
  fun tearDown() {
    server.stop(0)
  }

  private val dataSource = NativeAppTemplatePreferencesDataSource(
    InMemoryDataStore(
      UserPreferences.newBuilder()
        .setAccountId(ACCOUNT_ID)
        .setToken("test-token")
        .setClient("test-client")
        .setUid("john@example.com")
        .setExpiry("1767225600")
        .setIsLoggedIn(true)
        .build(),
    ),
  )

  private val retrofit: Retrofit = NetModule().let { module ->
    val okHttp = module.provideOkHttp(
      loggingInterceptor = sessionRedactingLoggingInterceptor(HttpLoggingInterceptor.Level.NONE),
      // The API host as the client reaches this server.
      authInterceptor = AuthInterceptor(dataSource, apiHost = "127.0.0.1"),
      acceptLanguageInterceptor = AcceptLanguageInterceptor { listOf("ja-JP", "en-US") },
    )
    // NetModule's converter and call adapter, pointed at the local server.
    module.provideRetrofit(okHttp).newBuilder()
      .baseUrl("http://127.0.0.1:${server.address.port}/")
      .build()
  }

  private val itemTagRepository = ItemTagRepositoryImpl(dataSource, ItemTagApi.create(retrofit), Dispatchers.IO)
  private val shopRepository = ShopRepositoryImpl(dataSource, ShopApi.create(retrofit), Dispatchers.IO)
  private val loginRepository = LoginRepositoryImpl(LoginApi.create(retrofit), dataSource, Dispatchers.IO)
  private val signUpRepository = SignUpRepositoryImpl(SignUpApi.create(retrofit), Dispatchers.IO)
  private val accountPasswordRepository = AccountPasswordRepositoryImpl(dataSource, AccountPasswordApi.create(retrofit), Dispatchers.IO)

  private fun respond(status: Int, body: String) {
    this.status = status
    this.responseBody = body
  }

  @Test
  fun getItemTags_requestsThePageOfTheShopInTheSignedInAccount() = runBlocking {
    respond(200, """{"data":[],"meta":{"current_page":2,"total_pages":3}}""")

    val itemTags = itemTagRepository.getItemTags(SHOP_ID, page = 2).first()

    assertEquals("GET", received.single().method)
    assertEquals("/$ACCOUNT_ID/api/v1/shopkeeper/shops/$SHOP_ID/item_tags?page=2", received.single().pathAndQuery)
    assertEquals(2, itemTags.meta?.currentPage)
    assertEquals(3, itemTags.meta?.totalPages)
  }

  @Test
  fun getItemTags_withoutAPage_sendsNoPageParameter() = runBlocking {
    respond(200, """{"data":[]}""")

    itemTagRepository.getItemTags(SHOP_ID, page = null).first()

    // Retrofit leaves out a @Query whose value is null.
    assertEquals("/$ACCOUNT_ID/api/v1/shopkeeper/shops/$SHOP_ID/item_tags", received.single().pathAndQuery)
  }

  @Test
  fun everyApiRequest_carriesTheSessionAndTheDeviceLanguages() = runBlocking {
    respond(200, """{"data":[]}""")

    itemTagRepository.getItemTags(SHOP_ID, page = null).first()

    val headers = received.single().headers
    // RequestHelper.getHeaders() with the stored session.
    assertEquals("test-token", headers["access-token"])
    assertEquals("test-client", headers["client"])
    assertEquals("john@example.com", headers["uid"])
    assertEquals("1767225600", headers["expiry"])
    assertEquals("android", headers["source"])
    // acceptLanguageHeader(["ja-JP", "en-US"]): the first tag, then q = 0.9.
    assertEquals("ja-JP, en-US;q=0.9", headers["accept-language"])
  }

  @Test
  fun updateItemTag_sendsAnEmptyDescription() = runBlocking {
    respond(200, """{"data":{"id":"$ITEM_TAG_ID","type":"item_tag","attributes":{"name":"A001","description":""}}}""")

    itemTagRepository.updateItemTag(ITEM_TAG_ID, ItemTagBody(ItemTagBodyDetail(name = "A001", description = ""))).first()

    val request = received.single()
    assertEquals("PATCH", request.method)
    assertEquals("/$ACCOUNT_ID/api/v1/shopkeeper/item_tags/$ITEM_TAG_ID", request.pathAndQuery)
    // ItemTagBodyDetail.description has no default, so "" is sent and the server clears it
    // (the API updates only the attributes present).
    assertEquals("""{"item_tag":{"name":"A001","description":""}}""", request.body)
  }

  @Test
  fun completeAndIdleItemTag_patchTheirStateRoutes() = runBlocking {
    respond(200, """{"data":{"id":"$ITEM_TAG_ID","type":"item_tag","attributes":{"state":"completed"}}}""")

    val completed = itemTagRepository.completeItemTag(ITEM_TAG_ID).first()
    itemTagRepository.idleItemTag(ITEM_TAG_ID).first()

    assertEquals(listOf("PATCH", "PATCH"), received.map { it.method })
    assertEquals(
      listOf(
        "/$ACCOUNT_ID/api/v1/shopkeeper/item_tags/$ITEM_TAG_ID/complete",
        "/$ACCOUNT_ID/api/v1/shopkeeper/item_tags/$ITEM_TAG_ID/idle",
      ),
      received.map { it.pathAndQuery },
    )
    assertEquals("completed", completed.getState())
  }

  @Test
  fun createShop_sendsANullDescriptionAsNull() = runBlocking {
    respond(201, """{"data":{"id":"$SHOP_ID","type":"shop","attributes":{"name":"8th & Townsend"}}}""")

    val shop = shopRepository.createShop(ShopBody(ShopBodyDetail(name = "8th & Townsend", description = null, timeZone = "Tokyo"))).first()

    val request = received.single()
    assertEquals("POST", request.method)
    assertEquals("/$ACCOUNT_ID/api/v1/shopkeeper/shops", request.pathAndQuery)
    // ShopBodyDetail.description has no default, so it is always written; kotlinx.serialization's
    // explicitNulls (true unless the Json config turns it off) writes null as null.
    assertEquals("""{"shop":{"name":"8th & Townsend","description":null,"time_zone":"Tokyo"}}""", request.body)
    assertEquals("8th & Townsend", shop.getName())
  }

  @Test
  fun deleteShop_emitsTrueForAnyStatusBody() = runBlocking {
    respond(200, """{"status":200}""")

    val deleted = shopRepository.deleteShop(SHOP_ID).first()

    assertEquals("DELETE", received.single().method)
    assertEquals("/$ACCOUNT_ID/api/v1/shopkeeper/shops/$SHOP_ID", received.single().pathAndQuery)
    assertTrue(deleted)
  }

  @Test
  fun getPermissions_requestsTheSignedInAccountsPermissions() = runBlocking {
    respond(200, """{"data":[],"meta":{"android_app_version":7,"should_update_privacy":true,"should_update_terms":false,"shop_limit_count":10}}""")

    val permissions = loginRepository.getPermissions().first()

    assertEquals("GET", received.single().method)
    assertEquals("/$ACCOUNT_ID/api/v1/shopkeeper/permissions", received.single().pathAndQuery)
    assertEquals(7, permissions.getAndroidAppVersion())
    assertEquals(true, permissions.getShouldUpdatePrivacy())
    assertEquals(10, permissions.getShopLimitCount())
  }

  @Test
  fun confirmingThePrivacyAndTerms_patchesTheirRoutes() = runBlocking {
    respond(200, """{"status":200}""")

    assertTrue(loginRepository.updateConfirmedPrivacyVersion().first())
    assertTrue(loginRepository.updateConfirmedTermsVersion().first())

    assertEquals(
      listOf(
        "PATCH /$ACCOUNT_ID/api/v1/shopkeeper/me/update_confirmed_privacy_version",
        "PATCH /$ACCOUNT_ID/api/v1/shopkeeper/me/update_confirmed_terms_version",
      ),
      received.map { "${it.method} ${it.pathAndQuery}" },
    )
  }

  @Test
  fun logout_clearsTheStoredSession() = runBlocking {
    respond(200, """{"success":true}""")

    loginRepository.logout().first()

    assertEquals("DELETE /shopkeeper_auth/sign_out", received.single().let { "${it.method} ${it.pathAndQuery}" })
    // clearUserPreferences resets UserPreferences to its defaults.
    val userData = dataSource.userData.first()
    assertFalse(userData.isLoggedIn)
    assertEquals("", userData.token)
    assertEquals("", userData.accountId)
  }

  @Test
  fun logout_whenTheServerFails_stillClearsTheStoredSession() = runBlocking {
    respond(500, """{"errors":["Internal Server Error"]}""")

    assertFailsWith<ApiException.UnprocessableError> { loginRepository.logout().first() }

    assertFalse(dataSource.userData.first().isLoggedIn)
    assertEquals("", dataSource.userData.first().token)
  }

  @Test
  fun updateLocale_patchesOnlyTheLocale() = runBlocking {
    respond(200, """{"data":{"id":"shopkeeper-1","attributes":{"locale":"ja"}}}""")

    val shopkeeper = signUpRepository.updateLocale("ja").first()

    assertEquals("PATCH /shopkeeper_auth", received.single().let { "${it.method} ${it.pathAndQuery}" })
    assertEquals("""{"locale":"ja"}""", received.single().body)
    assertEquals("ja", shopkeeper.getLocale())
  }

  @Test
  fun updateAccountPassword_patchesThePasswordOfTheSignedInAccount() = runBlocking {
    respond(200, """{"status":200}""")

    val updated = accountPasswordRepository.updateAccountPassword(
      UpdatePasswordBody(UpdatePasswordBodyDetail(currentPassword = "old-password", password = "new-password", passwordConfirmation = "new-password")),
    ).first()

    assertEquals("PATCH /$ACCOUNT_ID/api/v1/shopkeeper/account/password", received.single().let { "${it.method} ${it.pathAndQuery}" })
    assertEquals(
      """{"shopkeeper":{"current_password":"old-password","password":"new-password","password_confirmation":"new-password"}}""",
      received.single().body,
    )
    assertTrue(updated)
  }

  // The remaining calls' routes, so a refactoring of the Api interfaces or repositories cannot
  // silently move one. Each answer is "{}", which every response model decodes (all fields optional).
  @Test
  fun eachOtherCall_usesItsRoute() = runBlocking {
    respond(200, "{}")
    val itemTagBody = ItemTagBody(ItemTagBodyDetail(name = "A001", description = ""))
    val shopUpdateBody = ShopUpdateBody(ShopUpdateBodyDetail(name = "8th & Townsend", description = "", timeZone = "Tokyo"))

    shopRepository.getShops().first()
    shopRepository.getShop(SHOP_ID).first()
    shopRepository.updateShop(SHOP_ID, shopUpdateBody).first()
    itemTagRepository.getItemTag(ITEM_TAG_ID).first()
    itemTagRepository.createItemTag(SHOP_ID, itemTagBody).first()
    itemTagRepository.deleteItemTag(ITEM_TAG_ID).first()
    signUpRepository.updateAccount(SignUpForUpdate(name = "John", email = "john@example.com", timeZone = "Tokyo", locale = "en")).first()
    signUpRepository.deleteAccount().first()
    signUpRepository.sendResetPasswordInstruction(SendResetPassword(email = "john@example.com", redirectUrl = "https://example.com/r")).first()
    signUpRepository.sendConfirmationInstruction(SendConfirmation(email = "john@example.com", redirectUrl = "https://example.com/c")).first()

    assertEquals(
      listOf(
        "GET /$ACCOUNT_ID/api/v1/shopkeeper/shops",
        "GET /$ACCOUNT_ID/api/v1/shopkeeper/shops/$SHOP_ID",
        "PATCH /$ACCOUNT_ID/api/v1/shopkeeper/shops/$SHOP_ID",
        "GET /$ACCOUNT_ID/api/v1/shopkeeper/item_tags/$ITEM_TAG_ID",
        "POST /$ACCOUNT_ID/api/v1/shopkeeper/shops/$SHOP_ID/item_tags",
        "DELETE /$ACCOUNT_ID/api/v1/shopkeeper/item_tags/$ITEM_TAG_ID",
        "PATCH /shopkeeper_auth",
        "DELETE /shopkeeper_auth",
        "POST /shopkeeper_auth/password",
        "POST /shopkeeper_auth/confirmation",
      ),
      received.map { "${it.method} ${it.pathAndQuery}" },
    )
  }

  @Test
  fun signUp_postsTheShopkeeperWithTheAppLanguage() = runBlocking {
    respond(200, """{"data":{"id":"shopkeeper-1","attributes":{"email":"john@example.com"}}}""")

    signUpRepository.signUp(
      SignUp(name = "John", email = "john@example.com", timeZone = "Tokyo", password = "password", currentPlatform = "android", locale = "ja"),
    ).first()

    assertEquals("POST /shopkeeper_auth", received.single().let { "${it.method} ${it.pathAndQuery}" })
    // SignUp's properties in declaration order, with their @SerialName.
    assertEquals(
      """{"name":"John","email":"john@example.com","time_zone":"Tokyo","password":"password","current_platform":"android","locale":"ja"}""",
      received.single().body,
    )
  }

  @Test
  fun aValidationError_becomesAnApiErrorWithTheServerMessage() = runBlocking {
    // The API's error body: {"code": ..., "error_message": ...} (NativeAppTemplateApiError).
    respond(422, """{"code":422,"error_message":"Name has already been taken"}""")

    val exception = assertFailsWith<ApiException.ApiError> {
      shopRepository.createShop(ShopBody(ShopBodyDetail(name = "Taken", description = "", timeZone = "Tokyo"))).first()
    }

    assertEquals("Name has already been taken", exception.apiMessage)
    assertEquals(422, exception.httpStatusCode)
    // ApiException.ApiError: "[NATIVEAPPTEMPLATE-2001] " + "$apiMessage [Status: $code]".
    assertEquals("[NATIVEAPPTEMPLATE-2001] Name has already been taken [Status: 422]", exception.formattedDescription)
  }

  @Test
  fun aRejectedSession_isUnauthorized() = runBlocking {
    // devise_token_auth's answer to an expired or revoked token.
    respond(401, """{"errors":["You need to sign in or sign up before continuing."]}""")

    val exception = assertFailsWith<ApiException.UnprocessableError> { shopRepository.getShops().first() }

    assertTrue(exception.isUnauthorized)
    assertEquals("You need to sign in or sign up before continuing.", exception.rawMessage)
  }

  @Test
  fun aSignedOutRequest_carriesNoTokenAndNoAccountSegment() = runBlocking {
    dataSource.clearUserPreferences()
    respond(200, """{"data":[]}""")

    shopRepository.getShops().first()

    // RequestHelper adds the session headers only when there is a token.
    assertNull(received.single().headers["access-token"])
    // Current behavior, not a contract: the empty account id turns "{account_id}/api/..." into
    // "/api/...", which Retrofit resolves (RFC 3986, an absolute path) to the host root, so the
    // request goes to a route without the account segment instead of failing locally.
    assertEquals("/api/v1/shopkeeper/shops", received.single().pathAndQuery)
  }
}

private const val ACCOUNT_ID = "2140BC6B-1830-45EE-96A4-B4ED5F53AC11"
private const val SHOP_ID = "5712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val ITEM_TAG_ID = "9712F2DF-DFC7-A3AA-66BC-191203654A1A"
