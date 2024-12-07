package com.nativeapptemplate.nativeapptemplatefree.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.nativeapptemplate.nativeapptemplatefree.BuildConfig
import com.nativeapptemplate.nativeapptemplatefree.DarkThemeConfigProto
import com.nativeapptemplate.nativeapptemplatefree.UserPreferences
import com.nativeapptemplate.nativeapptemplatefree.copy
import com.nativeapptemplate.nativeapptemplatefree.model.DarkThemeConfig
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import com.nativeapptemplate.nativeapptemplatefree.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class NatPreferencesDataSource @Inject constructor(
  private val userPreferences: DataStore<UserPreferences>,
) {
  val userData = userPreferences.data
    .map {
      UserData(
        id = it.id,
        accountId = it.accountId,
        personalAccountId = it.personalAccountId,
        accountOwnerId = it.accountOwnerId,
        accountName = it.accountName,
        email = it.email,
        name = it.name,
        timeZone = it.timeZone,
        token = it.token,
        client = it.client,
        uid = it.uid,
        expiry = it.expiry,
        darkThemeConfig = when (it.darkThemeConfig) {
          null,
          DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
          DarkThemeConfigProto.UNRECOGNIZED,
          DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
          ->
            DarkThemeConfig.FOLLOW_SYSTEM
          DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT ->
            DarkThemeConfig.LIGHT
          DarkThemeConfigProto.DARK_THEME_CONFIG_DARK ->
            DarkThemeConfig.DARK
        },
        isLoggedIn = it.isLoggedIn,

        androidAppVersion = it.androidAppVersion,
        shouldUpdatePrivacy = it.shouldUpdatePrivacy,
        shouldUpdateTerms = it.shouldUpdateTerms,
        shopLimitCount = it.shopLimitCount,

        isEmailUpdated = it.isEmailUpdated,
        isMyAccountDeleted = it.isMyAccountDeleted,
      )
    }

  suspend fun setShopkeeper(loggedInShopkeeper: LoggedInShopkeeper) {
    try {
      userPreferences.updateData {
        it.copy {
          this.id = loggedInShopkeeper.getId()!!
          this.accountId = loggedInShopkeeper.getAccountId()!!
          this.personalAccountId = loggedInShopkeeper.getPersonalAccountId()!!
          this.accountOwnerId = loggedInShopkeeper.getAccountOwnerId()!!
          this.accountName = loggedInShopkeeper.getAccountName()!!
          this.email = loggedInShopkeeper.getEmail()!!
          this.name = loggedInShopkeeper.getName()!!
          this.timeZone = loggedInShopkeeper.getTimeZone()!!
          this.token = loggedInShopkeeper.getToken()!!
          this.client = loggedInShopkeeper.getClient()!!
          this.uid = loggedInShopkeeper.getUID()!!
          this.expiry = loggedInShopkeeper.getExpiry()!!
          this.isLoggedIn = true
        }
      }
    } catch (ioException: IOException) {
      Log.e("NatPreferences", "Failed to update user preferences", ioException)
      throw ioException
    }
  }

  suspend fun setShopkeeperForUpdate(loggedInShopkeeper: LoggedInShopkeeper) {
    try {
      userPreferences.updateData {
        it.copy {
          this.email = loggedInShopkeeper.getEmail()!!
          this.name = loggedInShopkeeper.getName()!!
          this.timeZone = loggedInShopkeeper.getTimeZone()!!
          this.uid = loggedInShopkeeper.getUID()!!
        }
      }
    } catch (ioException: IOException) {
      Log.e("NatPreferences", "Failed to update user preferences", ioException)
      throw ioException
    }
  }

  suspend fun setPermissions(permissions: Permissions) {
    try {

      userPreferences.updateData {
        it.copy {
          val androidAppVersion = permissions.getAndroidAppVersion()!!
          this.androidAppVersion = androidAppVersion
          this.shouldUpdateApp = BuildConfig.VERSION_CODE < androidAppVersion

          this.shouldUpdatePrivacy = permissions.getShouldUpdatePrivacy()!!
          this.shouldUpdateTerms = permissions.getShouldUpdateTerms()!!
          this.shopLimitCount = permissions.getShopLimitCount()!!
        }
      }
    } catch (ioException: IOException) {
      Log.e("NatPreferences", "Failed to update user preferences", ioException)
      throw ioException
    }
  }

  suspend fun setAccountId(accountId: String) {
    try {
      userPreferences.updateData {
        it.copy {
          this.accountId = accountId
        }
      }
    } catch (ioException: IOException) {
      Log.e("NatPreferences", "Failed to update user preferences", ioException)
      throw ioException
    }
  }

  suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
    try {
      userPreferences.updateData {
        it.copy {
          this.darkThemeConfig = when (darkThemeConfig) {
            DarkThemeConfig.FOLLOW_SYSTEM ->
              DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
            DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
            DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
          }
        }
      }
    } catch (ioException: IOException) {
      Log.e("NatPreferences", "Failed to update user preferences", ioException)
      throw ioException
    }
  }

  suspend fun setIsEmailUpdated(isEmailUpdated: Boolean) {
    try {
      userPreferences.updateData {
        it.copy { this.isEmailUpdated = isEmailUpdated }
      }
    } catch (ioException: IOException) {
      Log.e("NatPreferences", "Failed to update user preferences", ioException)
      throw ioException
    }
  }

  suspend fun setIsMyAccountDeleted(isMyAccountDeleted: Boolean) {
    try {
      userPreferences.updateData {
        it.copy { this.isMyAccountDeleted = isMyAccountDeleted }
      }
    } catch (ioException: IOException) {
      Log.e("NatPreferences", "Failed to update user preferences", ioException)
      throw ioException
    }
  }

  suspend fun setIsShopDeleted(isShopDeleted: Boolean) {
    try {
      userPreferences.updateData {
        it.copy { this.isShopDeleted = isShopDeleted }
      }
    } catch (ioException: IOException) {
      Log.e("NatPreferences", "Failed to update user preferences", ioException)
      throw ioException
    }
  }

  suspend fun clearUserPreferences() {
    try {
      userPreferences.updateData {
        it.toBuilder().clear().build()
      }
    } catch (ioException: IOException) {
      Log.e("NatPreferences", "Failed to clear user preferences", ioException)
      throw ioException
    }
  }

  fun isLoggedIn(): Flow<Boolean> = userPreferences.data
    .map { data ->
      data.isLoggedIn
    }

  fun shouldUpdateApp(): Flow<Boolean> = userPreferences.data
    .map { data ->
      data.shouldUpdateApp
    }

  fun shouldUpdatePrivacy(): Flow<Boolean> = userPreferences.data
    .map { data ->
      data.shouldUpdatePrivacy
    }

  fun shouldUpdateTerms(): Flow<Boolean> = userPreferences.data
    .map { data ->
      data.shouldUpdateTerms
    }

  fun isEmailUpdated(): Flow<Boolean> = userPreferences.data
    .map { data ->
      data.isEmailUpdated
    }

  fun isMyAccountDeleted(): Flow<Boolean> = userPreferences.data
    .map { data ->
      data.isMyAccountDeleted
    }

  fun isShopDeleted(): Flow<Boolean> = userPreferences.data
    .map { data ->
      data.isShopDeleted
    }
}