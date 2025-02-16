package com.nativeapptemplate.nativeapptemplatefree.demo.login

import com.nativeapptemplate.nativeapptemplatefree.demo.DemoAssetManagerImpl
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.Login
import com.nativeapptemplate.nativeapptemplatefree.model.Meta
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DemoLoginRepositoryTest {
  private lateinit var subject: DemoLoginRepository
  private val testDispatcher = StandardTestDispatcher()

  private val loggedInShopkeeperData = Data(
    id = "5712F2DF-DFC7-A3AA-66BC-191203654A1A",
    type = "shopkeeper_sign_in",
    attributes = Attributes(
      accountId = "2140BC6B-1830-45EE-96A4-B4ED5F53AC11",
      personalAccountId = "2140BC6B-1830-45EE-96A4-B4ED5F53AC11",
      accountOwnerId = "5712F2DF-DFC7-A3AA-66BC-191203654A1A",
      accountName = "Account1",
      email = "john@example.com",
      name = "John Smith",
      timeZone = "Tokyo",
      uid = "john@example.com",
    ),
  )

  private val permissionsData = Permissions(
    datum = listOf(
      Data(
        id = "5712F2DF-DFC7-A3AA-66BC-191203654A1A",
        type = "permission",
        attributes = Attributes(
          name = "update shops",
          tag = "update_shops",
          createdAt = "2024-07-01T15:30:35.000Z",
          updatedAt = "2024-07-01T15:30:35.000Z"
        ),
      ),
      Data(
        id = "5712F2DF-DFC7-A3AA-66BC-191203654A1B",
        type = "permission",
        attributes = Attributes(
          name = "update organizations",
          tag = "update_organizations",
          createdAt = "2024-07-01T15:30:35.000Z",
          updatedAt = "2024-07-01T15:30:35.000Z"
        ),
      ),
      Data(
        id = "5712F2DF-DFC7-A3AA-66BC-191203654A1C",
        type = "permission",
        attributes = Attributes(
          name = "invitation",
          tag = "invitation",
          createdAt = "2024-07-01T15:30:35.000Z",
          updatedAt = "2024-07-01T15:30:35.000Z"
        ),
      ),
    ),
    meta = Meta(
      androidAppVersion = 1,
      shouldUpdatePrivacy = false,
      shouldUpdateTerms = false,
      shopLimitCount = 99,
    )
  )

  @Before
  fun setUp() {
    subject = DemoLoginRepository(
      ioDispatcher = testDispatcher,
      networkJson = Json { ignoreUnknownKeys = true },
      assets = DemoAssetManagerImpl,
    )
  }

  @Test
  fun testDeserializationOfLoggedInShopkeeperFromLogin() = runTest(testDispatcher) {
    kotlin.test.assertEquals(
      loggedInShopkeeperData,
      subject.login(Login(email = "john@example.com", password = "password")).first().datum,
    )
  }

  @Test
  fun testLogout() = runTest(testDispatcher) {
    kotlin.test.assertTrue(subject.logout().first())
  }

  @Test
  fun testDeserializationOfPermissions() = runTest(testDispatcher) {
    kotlin.test.assertEquals(
      permissionsData,
      subject.getPermissions().first(),
    )
  }

  @Test
  fun testUpdateConfirmedPrivacyVersion() = runTest(testDispatcher) {
    kotlin.test.assertTrue(subject.updateConfirmedPrivacyVersion().first())
  }

  @Test
  fun testUpdateConfirmedTermsVersion() = runTest(testDispatcher) {
    kotlin.test.assertTrue(subject.updateConfirmedTermsVersion().first())
  }
}
