package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.nativeapptemplate.nativeapptemplatefree.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The alerts shown after Forgot Password and Resend Confirmation Instructions. The English text
 * says what values-ja says: "パスワード再設定の案内を記載したメールを送信しました。" and
 * "メールアドレスの確認方法を記載したメールを送信しました。" (an email with the instructions was sent).
 * It used to read "An email has been sent the email containing ...", the reset one with a stray `"`.
 */
@RunWith(RobolectricTestRunner::class)
class SentInstructionsMessagesTest {
  private val context = ApplicationProvider.getApplicationContext<Context>()

  @Test
  fun sentResetPasswordInstruction_isOneCleanSentence() {
    assertEquals(
      "An email with instructions for resetting your password has been sent.",
      context.getString(R.string.sent_reset_password_instruction),
    )
  }

  @Test
  fun sentConfirmationInstruction_isOneCleanSentence() {
    assertEquals(
      "An email with instructions for confirming your email address has been sent.",
      context.getString(R.string.sent_confirmation_instruction),
    )
  }
}
