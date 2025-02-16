package com.nativeapptemplate.nativeapptemplatefree.demo.login

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.nativeapptemplate.nativeapptemplatefree.data.login.LoginRepository
import com.nativeapptemplate.nativeapptemplatefree.demo.DemoAssetManager
import com.nativeapptemplate.nativeapptemplatefree.demo.DemoAssetManagerImpl
import com.nativeapptemplate.nativeapptemplatefree.model.DarkThemeConfig
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Login
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import com.nativeapptemplate.nativeapptemplatefree.model.UserData
import com.nativeapptemplate.nativeapptemplatefree.network.Dispatcher
import com.nativeapptemplate.nativeapptemplatefree.network.NatDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

class DemoLoginRepository @Inject constructor(
  @Dispatcher(NatDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  private val networkJson: Json,
  private val assets: DemoAssetManager = DemoAssetManagerImpl,
) : LoginRepository {
  private val _userData = MutableSharedFlow<UserData>(replay = 1, onBufferOverflow = DROP_OLDEST)
  override val userData: Flow<UserData> = _userData.filterNotNull()

  private val loggedInShopkeeperFlow: Flow<LoggedInShopkeeper> = flow {
    val loggedInShopkeeper = getDataFromJsonFile<LoggedInShopkeeper>(LOGGED_IN_SHOPKEEPER_ASSET)
    emit(loggedInShopkeeper)
  }.flowOn(ioDispatcher)

  private val permissionsFlow: Flow<Permissions> = flow {
    val permissions = getDataFromJsonFile<Permissions>(PERMISSIONS_ASSET)
    emit(permissions)
  }.flowOn(ioDispatcher)

  override fun login(login: Login): Flow<LoggedInShopkeeper> = loggedInShopkeeperFlow

  override fun logout(): Flow<Boolean> = MutableStateFlow(true)

  override fun getPermissions(): Flow<Permissions> = permissionsFlow

  override fun updateConfirmedPrivacyVersion(): Flow<Boolean> = MutableStateFlow(true)

  override fun updateConfirmedTermsVersion(): Flow<Boolean> = MutableStateFlow(true)

  override suspend fun setAccountId(accountId: String) {
  }

  override suspend fun setShopkeeper(loggedInShopkeeper: LoggedInShopkeeper) {
  }

  override suspend fun setShopkeeperForUpdate(loggedInShopkeeper: LoggedInShopkeeper) {
  }

  override suspend fun setPermissions(permissions: Permissions) {
  }

  override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
  }

  override suspend fun setIsEmailUpdated(isEmailUpdated: Boolean) {
  }

  override suspend fun setIsMyAccountDeleted(isMyAccountDeleted: Boolean) {
  }

  override suspend fun setIsShopDeleted(isShopDeleted: Boolean) {
  }

  override suspend fun clearUserPreferences() {
  }

  override fun isLoggedIn(): Flow<Boolean> = MutableStateFlow(true)

  override fun shouldUpdateApp(): Flow<Boolean> = MutableStateFlow(true)

  override fun shouldUpdatePrivacy(): Flow<Boolean> = MutableStateFlow(true)

  override fun shouldUpdateTerms(): Flow<Boolean> = MutableStateFlow(true)

  override fun isEmailUpdated(): Flow<Boolean> = MutableStateFlow(true)

  override fun isMyAccountDeleted(): Flow<Boolean> = MutableStateFlow(true)

  override fun isShopDeleted(): Flow<Boolean> = MutableStateFlow(true)

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
    private const val PERMISSIONS_ASSET = "permissions.json"
  }
}
