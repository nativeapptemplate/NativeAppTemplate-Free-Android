package com.nativeapptemplate.nativeapptemplatefree.demo.login

import com.nativeapptemplate.nativeapptemplatefree.NatConstants
import com.nativeapptemplate.nativeapptemplatefree.demo.DemoAssetManagerImpl
import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.SendConfirmation
import com.nativeapptemplate.nativeapptemplatefree.model.SendResetPassword
import com.nativeapptemplate.nativeapptemplatefree.model.SignUp
import com.nativeapptemplate.nativeapptemplatefree.model.SignUpForUpdate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DemoSignUpRepositoryTest {
  private lateinit var subject: DemoSignUpRepository
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

  @Before
  fun setUp() {
    subject = DemoSignUpRepository(
      ioDispatcher = testDispatcher,
      networkJson = Json { ignoreUnknownKeys = true },
      assets = DemoAssetManagerImpl,
    )
  }

  @Test
  fun testDeserializationOfLoggedInShopkeeperFromSignUp() = runTest(testDispatcher) {
    kotlin.test.assertEquals(
      LoggedInShopkeeper(datum = loggedInShopkeeperData),
      subject.signUp(
        SignUp(
          name = "John Smith",
          email = "john@example.com",
          password = "password",
          timeZone = "Tokyo",
          currentPlatform = "android"
        ),
      ).first(),
    )
  }

  @Test
  fun testDeserializationOfLoggedInShopkeeperFromUpdatingAccount() = runTest(testDispatcher) {
    kotlin.test.assertEquals(
      LoggedInShopkeeper(datum = loggedInShopkeeperData),
      subject.updateAccount(
        SignUpForUpdate(
          name = "John Smith",
          email = "john@example.com",
          timeZone = "Tokyo",
        ),
      ).first(),
    )
  }

  @Test
  fun testDeleteOfAccount() = runTest(testDispatcher) {
    kotlin.test.assertTrue(subject.deleteAccount().first())
  }

  @Test
  fun testSendResetPasswordInstruction() = runTest(testDispatcher) {
    kotlin.test.assertTrue(
      subject.sendResetPasswordInstruction(
        SendResetPassword(
          email = "john@example.com",
          redirectUrl = SendResetPassword.redirectUrlString(NatConstants.baseUrlString()),
        )
      ).first()
    )
  }

  @Test
  fun testSendConfirmationInstruction() = runTest(testDispatcher) {
    kotlin.test.assertTrue(
      subject.sendConfirmationInstruction(
        SendConfirmation(
          email = "john@example.com",
          redirectUrl = SendConfirmation.redirectUrlString(NatConstants.baseUrlString()),
        )
      ).first()
    )
  }
}
