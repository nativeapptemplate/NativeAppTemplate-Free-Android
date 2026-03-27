package com.nativeapptemplate.nativeapptemplatefree.data.login

import com.nativeapptemplate.nativeapptemplatefree.model.SendConfirmation
import com.nativeapptemplate.nativeapptemplatefree.model.SendResetPassword
import com.nativeapptemplate.nativeapptemplatefree.model.SignUp
import com.nativeapptemplate.nativeapptemplatefree.model.SignUpForUpdate
import com.nativeapptemplate.nativeapptemplatefree.model.Status
import com.nativeapptemplate.nativeapptemplatefree.network.Dispatcher
import com.nativeapptemplate.nativeapptemplatefree.network.NatDispatchers
import com.nativeapptemplate.nativeapptemplatefree.network.emitApiResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(
  private val api: SignUpApi,
  @Dispatcher(NatDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : SignUpRepository {
  override fun signUp(
    signUp: SignUp,
  ) = flow {
    val response = api.signUp(signUp)
    emitApiResponse(response)
  }.flowOn(ioDispatcher)

  override fun updateAccount(
    signUpForUpdate: SignUpForUpdate,
  ) = flow {
    val response = api.updateAccount(signUpForUpdate)
    emitApiResponse(response)
  }.flowOn(ioDispatcher)

  override fun deleteAccount() = flow {
    val response = api.deleteAccount()
    emitApiResponse<Status, Boolean>(response) { true }
  }.flowOn(ioDispatcher)

  override fun sendResetPasswordInstruction(
    sendResetPassword: SendResetPassword,
  ) = flow {
    val response = api.sendResetPasswordInstruction(sendResetPassword)
    emitApiResponse<Status, Boolean>(response) { true }
  }.flowOn(ioDispatcher)

  override fun sendConfirmationInstruction(
    sendConfirmation: SendConfirmation,
  ) = flow {
    val response = api.sendConfirmationInstruction(sendConfirmation)
    emitApiResponse<Status, Boolean>(response) { true }
  }.flowOn(ioDispatcher)
}
