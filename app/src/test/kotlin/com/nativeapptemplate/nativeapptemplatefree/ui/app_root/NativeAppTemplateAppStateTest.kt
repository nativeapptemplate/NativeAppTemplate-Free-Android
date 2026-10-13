package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.nativeapptemplate.nativeapptemplatefree.BuildConfig
import com.nativeapptemplate.nativeapptemplatefree.UserPreferences
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginApi
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepositoryImpl
import com.nativeapptemplate.nativeapptemplatefree.datastore.NativeAppTemplatePreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.datastoreTest.InMemoryDataStore
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Login
import com.nativeapptemplate.nativeapptemplatefree.model.Meta
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import com.nativeapptemplate.nativeapptemplatefree.model.Status
import com.nativeapptemplate.nativeapptemplatefree.ui.app_root.navigation.AuthDestination
import com.nativeapptemplate.nativeapptemplatefree.utils.NetworkMonitor
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The app-wide state the root composable routes and draws from, over the real preferences
 * (LoginRepositoryImpl + NativeAppTemplatePreferencesDataSource), as sign-in and the permissions
 * response change them. Robolectric for the NavHostController.
 */
@RunWith(RobolectricTestRunner::class)
class NativeAppTemplateAppStateTest {
  private val testScope = TestScope(UnconfinedTestDispatcher())
  private val dataSource = NativeAppTemplatePreferencesDataSource(InMemoryDataStore(UserPreferences.getDefaultInstance()))
  private val loginRepository = LoginRepositoryImpl(UnusedLoginApi, dataSource, UnconfinedTestDispatcher())
  private val isOnline = MutableStateFlow(true)

  private fun appState(scope: CoroutineScope) = NativeAppTemplateAppState(
    loginRepository = loginRepository,
    navController = TestNavHostController(ApplicationProvider.getApplicationContext()),
    coroutineScope = scope,
    networkMonitor = object : NetworkMonitor {
      override val isOnline = this@NativeAppTemplateAppStateTest.isOnline
    },
  )

  // Null until the preferences load, so the app does not route to onboarding on placeholder values.
  @Test
  fun authDestination_beforeAnyoneCollects_isNull() = testScope.runTest {
    assertNull(appState(backgroundScope).authDestination.value)
  }

  @Test
  fun authDestination_followsSignInAndThePermissions() = testScope.runTest {
    val appState = appState(backgroundScope)
    backgroundScope.launch { appState.authDestination.collect() }

    assertEquals(AuthDestination.ONBOARDING, appState.authDestination.value)

    dataSource.setShopkeeper(signedInShopkeeper)
    assertEquals(AuthDestination.SHOPS, appState.authDestination.value)

    // authDestination(): an app update first, then privacy, then terms.
    dataSource.setPermissions(Permissions(meta = Meta(shouldUpdatePrivacy = true, shouldUpdateTerms = true)))
    assertEquals(AuthDestination.ACCEPT_PRIVACY, appState.authDestination.value)

    dataSource.setPermissions(Permissions(meta = Meta(androidAppVersion = BuildConfig.VERSION_CODE + 1)))
    assertEquals(AuthDestination.NEED_APP_UPDATES, appState.authDestination.value)

    dataSource.clearUserPreferences()
    assertEquals(AuthDestination.ONBOARDING, appState.authDestination.value)
  }

  @Test
  fun shouldShowBottomBar_onlyWhenSignedInWithNothingToAccept() = testScope.runTest {
    val appState = appState(backgroundScope)
    backgroundScope.launch { appState.shouldShowBottomBar.collect() }

    assertFalse(appState.shouldShowBottomBar.value)

    dataSource.setShopkeeper(signedInShopkeeper)
    assertTrue(appState.shouldShowBottomBar.value)

    dataSource.setPermissions(Permissions(meta = Meta(shouldUpdateTerms = true)))
    assertFalse(appState.shouldShowBottomBar.value)
  }

  @Test
  fun isOffline_isTheOppositeOfTheNetworkMonitor() = testScope.runTest {
    val appState = appState(backgroundScope)
    backgroundScope.launch { appState.isOffline.collect() }

    assertFalse(appState.isOffline.value)

    isOnline.value = false
    assertTrue(appState.isOffline.value)
  }

  @Test
  fun deletionFlags_comeFromThePreferences() = testScope.runTest {
    val appState = appState(backgroundScope)
    backgroundScope.launch { appState.isShopDeleted.collect() }
    backgroundScope.launch { appState.isMyAccountDeleted.collect() }
    backgroundScope.launch { appState.isEmailUpdated.collect() }

    dataSource.setIsShopDeleted(true)
    dataSource.setIsMyAccountDeleted(true)
    dataSource.setIsEmailUpdated(true)

    assertTrue(appState.isShopDeleted.value)
    assertTrue(appState.isMyAccountDeleted.value)
    assertTrue(appState.isEmailUpdated.value)
  }
}

private val signedInShopkeeper = LoggedInShopkeeper(
  datum = Data(
    id = "5712F2DF-DFC7-A3AA-66BC-191203654A1A",
    attributes = Attributes(
      accountId = "2140BC6B-1830-45EE-96A4-B4ED5F53AC11",
      personalAccountId = "2140BC6B-1830-45EE-96A4-B4ED5F53AC11",
      accountOwnerId = "5712F2DF-DFC7-A3AA-66BC-191203654A1A",
      email = "john@example.com",
      name = "John Smith",
      timeZone = "Tokyo",
      token = "token",
      client = "client",
      uid = "john@example.com",
      expiry = "1713165114",
    ),
  ),
)

/** The app state only reads the stored preferences; no request is made. */
private object UnusedLoginApi : LoginApi {
  override suspend fun login(data: Login): ApiResponse<LoggedInShopkeeper> = error("not used")
  override suspend fun logout(): ApiResponse<Status> = error("not used")
  override suspend fun getPermissions(accountId: String): ApiResponse<Permissions> = error("not used")
  override suspend fun updateConfirmedPrivacyVersion(accountId: String): ApiResponse<Status> = error("not used")
  override suspend fun updateConfirmedTermsVersion(accountId: String): ApiResponse<Status> = error("not used")
}
