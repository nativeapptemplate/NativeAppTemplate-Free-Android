package com.nativeapptemplate.nativeapptemplatefree.demo.login

import com.nativeapptemplate.nativeapptemplatefree.model.UpdatePasswordBody
import com.nativeapptemplate.nativeapptemplatefree.model.UpdatePasswordBodyDetail
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DemoAccountPasswordRepositoryTest {
  private lateinit var subject: DemoAccountPasswordRepository

  @Before
  fun setUp() {
    subject = DemoAccountPasswordRepository()
  }

  @Test
  fun testUpdatePassword() = runTest {
    kotlin.test.assertTrue(
      subject.updateAccountPassword(
        UpdatePasswordBody(
          UpdatePasswordBodyDetail(
            currentPassword = "password",
            password = "updatingPassword",
            passwordConfirmation = "updatingPassword",
          )
        )
      ).first()
    )
  }
}
