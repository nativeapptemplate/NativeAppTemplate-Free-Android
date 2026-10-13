package com.nativeapptemplate.nativeapptemplatefree.datastore

import com.nativeapptemplate.nativeapptemplatefree.BuildConfig
import com.nativeapptemplate.nativeapptemplatefree.UserPreferences
import com.nativeapptemplate.nativeapptemplatefree.datastoreTest.InMemoryDataStore
import com.nativeapptemplate.nativeapptemplatefree.di.modules.networkJson
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.DarkThemeConfig
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.Locales
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Meta
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NativeAppTemplatePreferencesDataSourceTest {
  private val testScope = TestScope(UnconfinedTestDispatcher())

  private lateinit var subject: NativeAppTemplatePreferencesDataSource

  @Before
  fun setup() {
    subject = NativeAppTemplatePreferencesDataSource(InMemoryDataStore(UserPreferences.getDefaultInstance()))
  }

  @Test
  fun isLoggedIn_isFalseByDefault() = testScope.runTest {
    assertFalse(subject.userData.first().isLoggedIn)
  }

  @Test
  fun isLoggedIn_whenSettingShopkeeper_becomesTrue() = testScope.runTest {
    assertFalse(subject.isLoggedIn().first())

    subject.setShopkeeper(testInputLoggedInShopkeeper)

    assertTrue(subject.isLoggedIn().first())
  }

  @Test
  fun setPermissions_withoutMeta_keepsThePreviousValues() = testScope.runTest {
    subject.setPermissions(Permissions(meta = Meta(androidAppVersion = 1, shouldUpdatePrivacy = true, shouldUpdateTerms = true, shopLimitCount = 99)))

    // The server omitted "meta".
    subject.setPermissions(Permissions(meta = null))

    assertTrue(subject.shouldUpdatePrivacy().first())
    assertTrue(subject.shouldUpdateTerms().first())
  }

  // A "meta" that omits some of its fields. A missing shop limit must not be read as 0 ("no shops
  // allowed"), and a missing android_app_version must not reset the stored version (and with it
  // shouldUpdateApp). setPermissions keeps the stored value for anything the response omits.
  @Test
  fun setPermissions_withMetaMissingSomeFields_keepsThePreviousValues() = testScope.runTest {
    subject.setPermissions(Permissions(meta = Meta(androidAppVersion = 1, shouldUpdatePrivacy = false, shouldUpdateTerms = false, shopLimitCount = 99)))

    subject.setPermissions(
      networkJson.decodeFromString<Permissions>("""{"data": [], "meta": {"should_update_privacy": true}}"""),
    )

    // Expected: 99 and 1, the values the first setPermissions above stored, since the second
    // response omits these keys.
    val userData = subject.userData.first()
    assertEquals(99, userData.shopLimitCount)
    assertEquals(1, userData.androidAppVersion)
    // The one key the response did send is stored.
    assertTrue(subject.shouldUpdatePrivacy().first())
  }

  @Test
  fun setShopkeeperForUpdate_withMissingFields_keepsThePreviousValues() = testScope.runTest {
    subject.setShopkeeper(testInputLoggedInShopkeeper)

    // An update response that only echoes the changed name.
    subject.setShopkeeperForUpdate(
      LoggedInShopkeeper(datum = Data(id = LOGGED_IN_SHOPKEEPER_ID, attributes = Attributes(name = "Jane Smith"))),
    )

    val userData = subject.userData.first()
    assertEquals("Jane Smith", userData.name)
    assertEquals(LOGGED_IN_SHOPKEEPER_EMAIL, userData.email)
    assertEquals(LOGGED_IN_SHOPKEEPER_TIME_ZONE, userData.timeZone)
    // uid is sent as an auth header; losing it would break every later request.
    assertEquals(LOGGED_IN_SHOPKEEPER_UID, userData.uid)
  }

  @Test
  fun locale_inASessionSavedBeforeTheAppStoredOne_isEnglish() = testScope.runTest {
    // Bytes as an app version without the locale field wrote them: a signed-in session, no field 50.
    val savedByThePreviousVersion = UserPreferences.newBuilder()
      .setIsLoggedIn(true)
      .setToken(LOGGED_IN_SHOPKEEPER_TOKEN)
      .setName(LOGGED_IN_SHOPKEEPER_NAME)
      .build()
      .toByteArray()
    val dataSource = NativeAppTemplatePreferencesDataSource(
      InMemoryDataStore(UserPreferences.parseFrom(savedByThePreviousVersion)),
    )

    val userData = dataSource.userData.first()

    // Locales.DEFAULT, the server's fallback language.
    assertEquals("en", userData.locale)
    // The rest of the session still loads.
    assertTrue(userData.isLoggedIn)
    assertEquals(LOGGED_IN_SHOPKEEPER_TOKEN, userData.token)
    assertEquals(LOGGED_IN_SHOPKEEPER_NAME, userData.name)
  }

  @Test
  fun setShopkeeper_storesTheLocaleFromTheSignInResponse() = testScope.runTest {
    val attributes = testInputLoggedInShopkeeperData.attributes!!.copy(locale = "ja")

    subject.setShopkeeper(LoggedInShopkeeper(datum = testInputLoggedInShopkeeperData.copy(attributes = attributes)))

    assertEquals("ja", subject.userData.first().locale)
  }

  @Test
  fun setShopkeeperForUpdate_storesTheReturnedLocale() = testScope.runTest {
    subject.setShopkeeper(testInputLoggedInShopkeeper)

    subject.setShopkeeperForUpdate(
      LoggedInShopkeeper(datum = Data(id = LOGGED_IN_SHOPKEEPER_ID, attributes = Attributes(locale = "ja"))),
    )

    assertEquals("ja", subject.userData.first().locale)
  }

  @Test
  fun setShopkeeperForUpdate_withoutALocale_keepsTheStoredOne() = testScope.runTest {
    val attributes = testInputLoggedInShopkeeperData.attributes!!.copy(locale = "ja")
    subject.setShopkeeper(LoggedInShopkeeper(datum = testInputLoggedInShopkeeperData.copy(attributes = attributes)))

    subject.setShopkeeperForUpdate(
      LoggedInShopkeeper(datum = Data(id = LOGGED_IN_SHOPKEEPER_ID, attributes = Attributes(name = "Jane Smith"))),
    )

    assertEquals("ja", subject.userData.first().locale)
  }

  @Test
  fun setShopkeeper_withoutTheOptionalAccountName_storesAnEmptyName() = testScope.runTest {
    // Only the session fields are required (LoginRepositoryImpl checks them); the rest may be absent.
    val attributes = testInputLoggedInShopkeeperData.attributes!!.copy(accountName = null)

    subject.setShopkeeper(LoggedInShopkeeper(datum = testInputLoggedInShopkeeperData.copy(attributes = attributes)))

    val userData = subject.userData.first()
    assertTrue(userData.isLoggedIn)
    assertEquals("", userData.accountName)
  }

  @Test
  fun setShopkeeper_storesTheSessionFromTheSignInResponse() = testScope.runTest {
    subject.setShopkeeper(testInputLoggedInShopkeeper)

    val userData = subject.userData.first()
    assertEquals(LOGGED_IN_SHOPKEEPER_ID, userData.id)
    assertEquals(LOGGED_IN_SHOPKEEPER_ACCOUNT_ID, userData.accountId)
    assertEquals(LOGGED_IN_SHOPKEEPER_TOKEN, userData.token)
    assertEquals(LOGGED_IN_SHOPKEEPER_CLIENT, userData.client)
    assertEquals(LOGGED_IN_SHOPKEEPER_UID, userData.uid)
    assertEquals(LOGGED_IN_SHOPKEEPER_EXPIRY, userData.expiry)
    assertEquals(LOGGED_IN_SHOPKEEPER_TIME_ZONE, userData.timeZone)
  }

  // shouldUpdateApp = this build's versionCode (BuildConfig.VERSION_CODE) is older than the
  // android_app_version the server requires.
  @Test
  fun setPermissions_aNewerRequiredAppVersion_requiresAnAppUpdate() = testScope.runTest {
    subject.setPermissions(Permissions(meta = Meta(androidAppVersion = BuildConfig.VERSION_CODE + 1)))

    assertTrue(subject.shouldUpdateApp().first())
    assertEquals(BuildConfig.VERSION_CODE + 1, subject.userData.first().androidAppVersion)
  }

  @Test
  fun setPermissions_theCurrentAppVersion_requiresNoAppUpdate() = testScope.runTest {
    subject.setPermissions(Permissions(meta = Meta(androidAppVersion = BuildConfig.VERSION_CODE + 1)))

    // The required version is this build's own: no longer older.
    subject.setPermissions(Permissions(meta = Meta(androidAppVersion = BuildConfig.VERSION_CODE)))

    assertFalse(subject.shouldUpdateApp().first())
  }

  @Test
  fun darkThemeConfig_isFollowSystemUntilSet_thenWhatWasSet() = testScope.runTest {
    // DARK_THEME_CONFIG_UNSPECIFIED (proto3's default 0) reads as FOLLOW_SYSTEM.
    assertEquals(DarkThemeConfig.FOLLOW_SYSTEM, subject.userData.first().darkThemeConfig)

    DarkThemeConfig.entries.forEach { config ->
      subject.setDarkThemeConfig(config)
      assertEquals(config, subject.userData.first().darkThemeConfig)
    }
  }

  @Test
  fun clearSession_forgetsEverythingAboutTheShopkeeper() = testScope.runTest {
    // Whoever signs in next on this device must see nothing of this shopkeeper.
    subject.setShopkeeper(LoggedInShopkeeper(datum = testInputLoggedInShopkeeperData.copy(attributes = testInputLoggedInShopkeeperData.attributes!!.copy(locale = "ja"))))
    subject.setPermissions(Permissions(meta = Meta(androidAppVersion = BuildConfig.VERSION_CODE + 1, shouldUpdatePrivacy = true, shouldUpdateTerms = true, shopLimitCount = 99)))
    subject.setIsEmailUpdated(true)
    subject.setIsMyAccountDeleted(true)
    subject.setIsShopDeleted(true)

    subject.clearSession()

    // Expected: the proto3 defaults ("" / 0 / false), as on a fresh install.
    val userData = subject.userData.first()
    assertFalse(userData.isLoggedIn)
    assertEquals("", userData.id)
    assertEquals("", userData.accountId)
    assertEquals("", userData.personalAccountId)
    assertEquals("", userData.accountOwnerId)
    assertEquals("", userData.accountName)
    assertEquals("", userData.email)
    assertEquals("", userData.name)
    assertEquals("", userData.timeZone)
    // An empty stored locale reads as Locales.DEFAULT.
    assertEquals(Locales.DEFAULT, userData.locale)
    assertEquals("", userData.token)
    assertEquals("", userData.client)
    assertEquals("", userData.uid)
    assertEquals("", userData.expiry)
    assertEquals(0, userData.androidAppVersion)
    assertEquals(0, userData.shopLimitCount)
    assertFalse(userData.shouldUpdatePrivacy)
    assertFalse(userData.shouldUpdateTerms)
    assertFalse(subject.shouldUpdateApp().first())
    assertFalse(userData.isEmailUpdated)
    assertFalse(userData.isMyAccountDeleted)
    assertFalse(subject.isShopDeleted().first())
  }

  @Test
  fun clearSession_keepsTheDeviceSettings() = testScope.runTest {
    // Chosen on this device, not for the account: signing in or out must not reset them.
    subject.setDarkThemeConfig(DarkThemeConfig.DARK)
    subject.setDidShowTapShopBelowTip(true)
    subject.setShopkeeper(testInputLoggedInShopkeeper)

    subject.clearSession()

    assertEquals(DarkThemeConfig.DARK, subject.userData.first().darkThemeConfig)
    assertTrue(subject.didShowTapShopBelowTip().first())
  }

  @Test
  fun flags_readBackWhatWasSet() = testScope.runTest {
    subject.setIsEmailUpdated(true)
    subject.setIsMyAccountDeleted(true)
    subject.setIsShopDeleted(true)
    subject.setDidShowTapShopBelowTip(true)

    assertTrue(subject.isEmailUpdated().first())
    assertTrue(subject.isMyAccountDeleted().first())
    assertTrue(subject.isShopDeleted().first())
    assertTrue(subject.didShowTapShopBelowTip().first())

    subject.setIsShopDeleted(false)
    assertFalse(subject.isShopDeleted().first())
  }
}

private const val LOGGED_IN_SHOPKEEPER_TYPE = "shopkeeper_sign_in"
private const val LOGGED_IN_SHOPKEEPER_ID = "5712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val LOGGED_IN_SHOPKEEPER_ACCOUNT_ID = "2140BC6B-1830-45EE-96A4-B4ED5F53AC11"
private const val LOGGED_IN_SHOPKEEPER_PERSONAL_ACCOUNT_ID = "2140BC6B-1830-45EE-96A4-B4ED5F53AC11"
private const val LOGGED_IN_SHOPKEEPER_ACCOUNT_OWNER_ID = "5712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val LOGGED_IN_SHOPKEEPER_ACCOUNT_NAME = "Account1"
private const val LOGGED_IN_SHOPKEEPER_EMAIL = "john@example.com"
private const val LOGGED_IN_SHOPKEEPER_NAME = "John Smith"
private const val LOGGED_IN_SHOPKEEPER_TIME_ZONE = "Tokyo"
private const val LOGGED_IN_SHOPKEEPER_TOKEN = "RVpFr8xbeiWRnj8cu7AeOP"
private const val LOGGED_IN_SHOPKEEPER_CLIENT = "Vd6GFW-9DaZrU2pzFd-Asa"
private const val LOGGED_IN_SHOPKEEPER_UID = "john@example.com"
private const val LOGGED_IN_SHOPKEEPER_EXPIRY = "1713165114"

private val testInputLoggedInShopkeeperData =
  Data(
    id = LOGGED_IN_SHOPKEEPER_ID,
    type = LOGGED_IN_SHOPKEEPER_TYPE,
    attributes = Attributes(
      accountId = LOGGED_IN_SHOPKEEPER_ACCOUNT_ID,
      personalAccountId = LOGGED_IN_SHOPKEEPER_PERSONAL_ACCOUNT_ID,
      accountOwnerId = LOGGED_IN_SHOPKEEPER_ACCOUNT_OWNER_ID,
      accountName = LOGGED_IN_SHOPKEEPER_ACCOUNT_NAME,
      email = LOGGED_IN_SHOPKEEPER_EMAIL,
      name = LOGGED_IN_SHOPKEEPER_NAME,
      timeZone = LOGGED_IN_SHOPKEEPER_TIME_ZONE,
      token = LOGGED_IN_SHOPKEEPER_TOKEN,
      client = LOGGED_IN_SHOPKEEPER_CLIENT,
      uid = LOGGED_IN_SHOPKEEPER_UID,
      expiry = LOGGED_IN_SHOPKEEPER_EXPIRY,
    ),
  )

private val testInputLoggedInShopkeeper = LoggedInShopkeeper(
  datum = testInputLoggedInShopkeeperData,
)
