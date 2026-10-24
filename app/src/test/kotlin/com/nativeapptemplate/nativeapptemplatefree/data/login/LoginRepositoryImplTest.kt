package com.nativeapptemplate.nativeapptemplatefree.data.login

import androidx.datastore.core.DataStore
import com.nativeapptemplate.nativeapptemplatefree.UserPreferences
import com.nativeapptemplate.nativeapptemplatefree.common.errors.ApiException
import com.nativeapptemplate.nativeapptemplatefree.common.errors.codedDescription
import com.nativeapptemplate.nativeapptemplatefree.datastore.NativeAppTemplatePreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.datastoreTest.InMemoryDataStore
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.DarkThemeConfig
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Login
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import com.nativeapptemplate.nativeapptemplatefree.model.Status
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import kotlin.test.assertFailsWith

/** Robolectric for android.util.Log (errors are logged where they are made) and the string resources. */
@RunWith(RobolectricTestRunner::class)
class LoginRepositoryImplTest {

  /** Every value written to the store, in order: one entry per DataStore write. */
  private val writes = mutableListOf<UserPreferences>()

  private val preferences = NativeAppTemplatePreferencesDataSource(
    object : DataStore<UserPreferences> {
      private val store = InMemoryDataStore(UserPreferences.getDefaultInstance())
      override val data = store.data
      override suspend fun updateData(transform: suspend (t: UserPreferences) -> UserPreferences) = store.updateData(transform).also { writes += it }
    },
  )

  private fun repository(
    loginResponse: LoggedInShopkeeper = sessionWith(),
    logoutResponse: ApiResponse<Status> = ApiResponse.Success(Status()),
  ) = LoginRepositoryImpl(
    api = object : LoginApi {
      override suspend fun login(data: Login): ApiResponse<LoggedInShopkeeper> = ApiResponse.Success(loginResponse)
      override suspend fun logout(): ApiResponse<Status> = logoutResponse
      override suspend fun getPermissions(accountId: String): ApiResponse<Permissions> = error("not used")
      override suspend fun updateConfirmedPrivacyVersion(accountId: String): ApiResponse<Status> = error("not used")
      override suspend fun updateConfirmedTermsVersion(accountId: String): ApiResponse<Status> = error("not used")
    },
    nativeAppTemplatePreferencesDataSource = preferences,
    ioDispatcher = UnconfinedTestDispatcher(),
  )

  @Test
  fun login_withACompleteSession_emitsIt() = runTest {
    val result = repository(sessionWith()).login(Login(email = "john@example.com", password = "password")).first()

    assertEquals("token", result.getToken())
  }

  @Test
  fun login_withoutAToken_failsTheFlowWithACodedError() = runTest {
    // Thrown inside the flow so the ViewModel's .catch shows it, instead of crashing when the
    // missing value is later dereferenced while saving the session.
    val exception = assertFailsWith<ApiException.UnprocessableError> {
      repository(sessionWith(token = null)).login(Login(email = "john@example.com", password = "password")).first()
    }
    // A response missing what the app needs is an internal error: values/strings.xml error_unexpected.
    // What is missing is a developer detail, in the message for the log.
    assertEquals("[NATIVEAPPTEMPLATE-2002] An unexpected error occurred.", exception.codedDescription)
    assertEquals("Not processable error(Incomplete sign-in response: missing token).", exception.message)
  }

  @Test
  fun login_withoutAnAccountId_failsTheFlowWithACodedError() = runTest {
    assertFailsWith<ApiException.UnprocessableError> {
      repository(sessionWith(accountId = null)).login(Login(email = "john@example.com", password = "password")).first()
    }
  }

  // Clearing the session routes to onboarding, which ends the calling screen's scope; the notice
  // must be in the very write that signs out, not a later one (SignOutNotice).
  @Test
  fun logout_withANotice_storesItInTheWriteThatSignsOut() = runTest {
    preferences.setShopkeeper(sessionWith())

    repository().logout(SignOutNotice.EMAIL_UPDATED).first()

    // (isLoggedIn, isEmailUpdated, isMyAccountDeleted) as written by the first signed-out write.
    assertEquals(Triple(false, true, false), firstSignedOutWrite())
  }

  @Test
  fun clearSession_withANotice_storesItInTheWriteThatSignsOut() = runTest {
    preferences.setShopkeeper(sessionWith())

    repository().clearSession(SignOutNotice.ACCOUNT_DELETED)

    assertEquals(Triple(false, false, true), firstSignedOutWrite())
  }

  @Test
  fun logout_withANotice_whenTheServerRejectsIt_stillStoresTheNotice() = runTest {
    // The email change already happened on the server; the "check your email" message must show.
    preferences.setShopkeeper(sessionWith())
    val response = ApiResponse.Failure.Error(
      Response.error<Status>(404, """{"success":false,"errors":["User was not found or was not logged in."]}""".toResponseBody("application/json".toMediaType())),
    )

    assertFailsWith<ApiException.UnprocessableError> {
      repository(logoutResponse = response).logout(SignOutNotice.EMAIL_UPDATED).first()
    }

    assertEquals(false, preferences.userData.first().isLoggedIn)
    assertEquals(true, preferences.userData.first().isEmailUpdated)
  }

  @Test
  fun logout_withoutANotice_clearsAnEarlierOne() = runTest {
    // One-shot flags belong to the ended session (clearSession) and must not reach the next one.
    preferences.setShopkeeper(sessionWith())
    preferences.setIsEmailUpdated(true)
    preferences.setIsMyAccountDeleted(true)

    repository().logout().first()

    assertEquals(false, preferences.userData.first().isEmailUpdated)
    assertEquals(false, preferences.userData.first().isMyAccountDeleted)
  }

  private fun firstSignedOutWrite() = writes.first { !it.isLoggedIn }.let { Triple(it.isLoggedIn, it.isEmailUpdated, it.isMyAccountDeleted) }

  @Test
  fun logout_keepsTheDarkTheme() = runTest {
    // A device setting: the next shopkeeper on this device keeps the theme chosen here.
    preferences.setDarkThemeConfig(DarkThemeConfig.DARK)
    preferences.setShopkeeper(sessionWith())

    repository().logout().first()

    assertEquals(false, preferences.userData.first().isLoggedIn)
    assertEquals(DarkThemeConfig.DARK, preferences.userData.first().darkThemeConfig)
  }

  @Test
  fun logout_whenTheServerRejectsIt_reportsTheServerMessage() = runTest {
    val response = ApiResponse.Failure.Error(
      Response.error<Status>(404, """{"success":false,"errors":["User was not found or was not logged in."]}""".toResponseBody("application/json".toMediaType())),
    )

    val exception = assertFailsWith<ApiException.UnprocessableError> {
      repository(logoutResponse = response).logout().first()
    }

    assertEquals("User was not found or was not logged in.", exception.rawMessage)
  }
}

private fun sessionWith(
  token: String? = "token",
  accountId: String? = "account-1",
) = LoggedInShopkeeper(
  datum = Data(
    id = "shopkeeper-1",
    attributes = Attributes(
      accountId = accountId,
      personalAccountId = "account-1",
      accountOwnerId = "shopkeeper-1",
      accountName = "Account",
      email = "john@example.com",
      name = "John",
      timeZone = "Tokyo",
      token = token,
      client = "client",
      uid = "john@example.com",
      expiry = "1713165114",
    ),
  ),
)
