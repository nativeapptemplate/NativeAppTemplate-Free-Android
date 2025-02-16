package com.nativeapptemplate.nativeapptemplatefree.demo.login

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.nativeapptemplate.nativeapptemplatefree.data.login.SignUpRepository
import com.nativeapptemplate.nativeapptemplatefree.demo.DemoAssetManager
import com.nativeapptemplate.nativeapptemplatefree.demo.DemoAssetManagerImpl
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.SendConfirmation
import com.nativeapptemplate.nativeapptemplatefree.model.SendResetPassword
import com.nativeapptemplate.nativeapptemplatefree.model.SignUp
import com.nativeapptemplate.nativeapptemplatefree.model.SignUpForUpdate
import com.nativeapptemplate.nativeapptemplatefree.network.Dispatcher
import com.nativeapptemplate.nativeapptemplatefree.network.NatDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

class DemoSignUpRepository @Inject constructor(
  @Dispatcher(NatDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  private val networkJson: Json,
  private val assets: DemoAssetManager = DemoAssetManagerImpl,
) : SignUpRepository {
  private val loggedInShopkeeperFlow: Flow<LoggedInShopkeeper> = flow {
    val loggedInShopkeeper = getDataFromJsonFile<LoggedInShopkeeper>(LOGGED_IN_SHOPKEEPER_ASSET)
    emit(loggedInShopkeeper)
  }.flowOn(ioDispatcher)

  override fun signUp(signUp: SignUp): Flow<LoggedInShopkeeper> = loggedInShopkeeperFlow

  override fun updateAccount(signUpForUpdate: SignUpForUpdate): Flow<LoggedInShopkeeper> = loggedInShopkeeperFlow

  override fun deleteAccount(): Flow<Boolean> = MutableStateFlow(true)

  override fun sendResetPasswordInstruction(sendResetPassword: SendResetPassword): Flow<Boolean> = MutableStateFlow(true)

  override fun sendConfirmationInstruction(sendConfirmation: SendConfirmation): Flow<Boolean> = MutableStateFlow(true)

  @OptIn(ExperimentalSerializationApi::class)
  private suspend inline fun <reified T> getDataFromJsonFile(fileName: String): T =
    withContext(ioDispatcher) {
      val context = ApplicationProvider.getApplicationContext<Context>()
      assets.open(context, fileName).use { inputStream ->
        networkJson.decodeFromStream(inputStream)
      }
    }

  companion object {
    private const val LOGGED_IN_SHOPKEEPER_ASSET = "logged_in_shopkeeper.json"
  }
}
