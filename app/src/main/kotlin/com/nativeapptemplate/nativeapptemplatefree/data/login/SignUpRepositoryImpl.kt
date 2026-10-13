package com.nativeapptemplate.nativeapptemplatefree.data.login

import com.nativeapptemplate.nativeapptemplatefree.model.SendConfirmation
import com.nativeapptemplate.nativeapptemplatefree.model.SendResetPassword
import com.nativeapptemplate.nativeapptemplatefree.model.ShopkeeperLocaleUpdate
import com.nativeapptemplate.nativeapptemplatefree.model.SignUp
import com.nativeapptemplate.nativeapptemplatefree.model.SignUpForUpdate
import com.nativeapptemplate.nativeapptemplatefree.network.Dispatcher
import com.nativeapptemplate.nativeapptemplatefree.network.NativeAppTemplateDispatchers
import com.nativeapptemplate.nativeapptemplatefree.network.apiAction
import com.nativeapptemplate.nativeapptemplatefree.network.apiRequest
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(
  private val api: SignUpApi,
  @param:Dispatcher(NativeAppTemplateDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : SignUpRepository {
  override fun signUp(
    signUp: SignUp,
  ) = apiRequest(ioDispatcher) {
    api.signUp(signUp)
  }

  override fun updateAccount(
    signUpForUpdate: SignUpForUpdate,
  ) = apiRequest(ioDispatcher) {
    api.updateAccount(signUpForUpdate)
  }

  override fun updateLocale(
    locale: String,
  ) = apiRequest(ioDispatcher) {
    api.updateLocale(ShopkeeperLocaleUpdate(locale = locale))
  }

  override fun deleteAccount() = apiAction(ioDispatcher) {
    api.deleteAccount()
  }

  override fun sendResetPasswordInstruction(
    sendResetPassword: SendResetPassword,
  ) = apiAction(ioDispatcher) {
    api.sendResetPasswordInstruction(sendResetPassword)
  }

  override fun sendConfirmationInstruction(
    sendConfirmation: SendConfirmation,
  ) = apiAction(ioDispatcher) {
    api.sendConfirmationInstruction(sendConfirmation)
  }
}
