package com.nativeapptemplate.nativeapptemplatefree.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.nativeapptemplate.nativeapptemplatefree.BuildConfig
import com.nativeapptemplate.nativeapptemplatefree.DarkThemeConfigProto
import com.nativeapptemplate.nativeapptemplatefree.UserPreferences
import com.nativeapptemplate.nativeapptemplatefree.copy
import com.nativeapptemplate.nativeapptemplatefree.model.DarkThemeConfig
import com.nativeapptemplate.nativeapptemplatefree.model.Locales
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import com.nativeapptemplate.nativeapptemplatefree.model.TimeZones
import com.nativeapptemplate.nativeapptemplatefree.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class NativeAppTemplatePreferencesDataSource @Inject constructor(
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
        // proto3 reads a missing string as "", so a session saved by an older app version gets the default.
        locale = it.locale.ifEmpty { Locales.DEFAULT },
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

  /**
   * The session fields (ids, token, client, uid, expiry) must be present; LoginRepositoryImpl
   * rejects a sign-in response without them before it reaches here.
   */
  suspend fun setShopkeeper(loggedInShopkeeper: LoggedInShopkeeper) {
    updatePreferences {
      it.copy {
        this.id = loggedInShopkeeper.getId()!!
        this.accountId = loggedInShopkeeper.getAccountId()!!
        this.personalAccountId = loggedInShopkeeper.getPersonalAccountId()!!
        this.accountOwnerId = loggedInShopkeeper.getAccountOwnerId()!!
        this.accountName = loggedInShopkeeper.getAccountName().orEmpty()
        this.email = loggedInShopkeeper.getEmail().orEmpty()
        this.name = loggedInShopkeeper.getName().orEmpty()
        this.timeZone = loggedInShopkeeper.getTimeZone() ?: TimeZones.DEFAULT_TIME_ZONE
        // Empty (read as Locales.DEFAULT) when the server does not send one.
        this.locale = loggedInShopkeeper.getLocale().orEmpty()
        this.token = loggedInShopkeeper.getToken()!!
        this.client = loggedInShopkeeper.getClient()!!
        this.uid = loggedInShopkeeper.getUID()!!
        this.expiry = loggedInShopkeeper.getExpiry()!!
        this.isLoggedIn = true
      }
    }
  }

  suspend fun setShopkeeperForUpdate(loggedInShopkeeper: LoggedInShopkeeper) {
    updatePreferences {
      it.copy {
        // Keep the stored value for anything the update response omits. Read the raw attributes:
        // the Data getters substitute defaults ("" or the default time zone) for missing values.
        val attributes = loggedInShopkeeper.datum?.attributes
        attributes?.email?.takeIf { it.isNotEmpty() }?.let { this.email = it }
        attributes?.name?.takeIf { it.isNotEmpty() }?.let { this.name = it }
        attributes?.timeZone?.takeIf { it.isNotEmpty() }?.let { this.timeZone = it }
        attributes?.locale?.takeIf { it.isNotEmpty() }?.let { this.locale = it }
        attributes?.uid?.takeIf { it.isNotEmpty() }?.let { this.uid = it }
      }
    }
  }

  suspend fun setPermissions(permissions: Permissions) {
    updatePreferences {
      it.copy {
        // Keep the stored value for anything the response omits (e.g. no "meta").
        permissions.getAndroidAppVersion()?.let { androidAppVersion ->
          this.androidAppVersion = androidAppVersion
          this.shouldUpdateApp = BuildConfig.VERSION_CODE < androidAppVersion
        }
        permissions.getShouldUpdatePrivacy()?.let { this.shouldUpdatePrivacy = it }
        permissions.getShouldUpdateTerms()?.let { this.shouldUpdateTerms = it }
        permissions.getShopLimitCount()?.let { this.shopLimitCount = it }
      }
    }
  }

  suspend fun setAccountId(accountId: String) {
    updatePreferences {
      it.copy {
        this.accountId = accountId
      }
    }
  }

  suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
    updatePreferences {
      it.copy {
        this.darkThemeConfig = when (darkThemeConfig) {
          DarkThemeConfig.FOLLOW_SYSTEM ->
            DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
          DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
          DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
        }
      }
    }
  }

  suspend fun setDidShowTapShopBelowTip(didShowTapShopBelowTip: Boolean) {
    updatePreferences {
      it.copy { this.didShowTapShopBelowTip = didShowTapShopBelowTip }
    }
  }

  suspend fun setIsEmailUpdated(isEmailUpdated: Boolean) {
    updatePreferences {
      it.copy { this.isEmailUpdated = isEmailUpdated }
    }
  }

  suspend fun setIsMyAccountDeleted(isMyAccountDeleted: Boolean) {
    updatePreferences {
      it.copy { this.isMyAccountDeleted = isMyAccountDeleted }
    }
  }

  suspend fun setIsShopDeleted(isShopDeleted: Boolean) {
    updatePreferences {
      it.copy { this.isShopDeleted = isShopDeleted }
    }
  }

  /**
   * Resets everything to its default except the device settings, which belong to whoever uses this
   * device rather than to the signed-in shopkeeper: the dark theme and the "tap shop below" tip
   * (an iOS TipKit tip, also per device). Everything else is the shopkeeper's (session, account,
   * permissions and limits, locale) or a one-shot flag about their session, and must not reach
   * whoever signs in next.
   *
   * [isEmailUpdated] and [isMyAccountDeleted] are set in the same write: they tell the signed-out
   * screen why the session ended, and the screen that ended it may be gone right after this write.
   */
  suspend fun clearSession(isEmailUpdated: Boolean = false, isMyAccountDeleted: Boolean = false) {
    updatePreferences("Failed to clear the session") {
      UserPreferences.newBuilder()
        .setDarkThemeConfig(it.darkThemeConfig)
        .setDidShowTapShopBelowTip(it.didShowTapShopBelowTip)
        .setIsEmailUpdated(isEmailUpdated)
        .setIsMyAccountDeleted(isMyAccountDeleted)
        .build()
    }
  }

  /** The account the signed-in shopkeeper is working in, which most API paths start with. */
  suspend fun currentAccountId(): String = userData.first().accountId

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

  fun didShowTapShopBelowTip(): Flow<Boolean> = userPreferences.data
    .map { data ->
      data.didShowTapShopBelowTip
    }

  /** Writes the preferences; a failed write is logged and rethrown. */
  private suspend fun updatePreferences(
    failureMessage: String = "Failed to update user preferences",
    transform: suspend (UserPreferences) -> UserPreferences,
  ) {
    try {
      userPreferences.updateData(transform)
    } catch (ioException: IOException) {
      Log.e("NativeAppTemplatePreferences", failureMessage, ioException)
      throw ioException
    }
  }
}
