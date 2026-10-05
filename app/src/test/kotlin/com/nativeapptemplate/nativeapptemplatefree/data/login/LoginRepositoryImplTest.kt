package com.nativeapptemplate.nativeapptemplatefree.data.login

import com.nativeapptemplate.nativeapptemplatefree.UserPreferences
import com.nativeapptemplate.nativeapptemplatefree.common.errors.ApiException
import com.nativeapptemplate.nativeapptemplatefree.datastore.NativeAppTemplatePreferencesDataSource
import com.nativeapptemplate.nativeapptemplatefree.datastoreTest.InMemoryDataStore
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Login
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import com.nativeapptemplate.nativeapptemplatefree.model.Status
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFailsWith

class LoginRepositoryImplTest {

  private fun repository(loginResponse: LoggedInShopkeeper) = LoginRepositoryImpl(
    api = object : LoginApi {
      override suspend fun login(data: Login): ApiResponse<LoggedInShopkeeper> = ApiResponse.Success(loginResponse)
      override suspend fun logout(): ApiResponse<Status> = error("not used")
      override suspend fun getPermissions(accountId: String): ApiResponse<Permissions> = error("not used")
      override suspend fun updateConfirmedPrivacyVersion(accountId: String): ApiResponse<Status> = error("not used")
      override suspend fun updateConfirmedTermsVersion(accountId: String): ApiResponse<Status> = error("not used")
    },
    natPreferencesDataSource = NativeAppTemplatePreferencesDataSource(
      InMemoryDataStore(UserPreferences.getDefaultInstance()),
    ),
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
    assertFailsWith<ApiException.UnprocessableError> {
      repository(sessionWith(token = null)).login(Login(email = "john@example.com", password = "password")).first()
    }
  }

  @Test
  fun login_withoutAnAccountId_failsTheFlowWithACodedError() = runTest {
    assertFailsWith<ApiException.UnprocessableError> {
      repository(sessionWith(accountId = null)).login(Login(email = "john@example.com", password = "password")).first()
    }
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
