package com.nativeapptemplate.nativeapptemplatefree.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.nativeapptemplate.nativeapptemplatefree.BuildConfig
import com.nativeapptemplate.nativeapptemplatefree.DarkThemeConfigProto
import com.nativeapptemplate.nativeapptemplatefree.ItemTagDataProto
import com.nativeapptemplate.nativeapptemplatefree.ItemTagInfoFromNdefMessageProto
import com.nativeapptemplate.nativeapptemplatefree.ScanResultProto
import com.nativeapptemplate.nativeapptemplatefree.UserPreferences
import com.nativeapptemplate.nativeapptemplatefree.copy
import com.nativeapptemplate.nativeapptemplatefree.model.CompleteScanResult
import com.nativeapptemplate.nativeapptemplatefree.model.CompleteScanResultType
import com.nativeapptemplate.nativeapptemplatefree.model.DarkThemeConfig
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagData
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagInfoFromNdefMessage
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagState
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagType
import com.nativeapptemplate.nativeapptemplatefree.model.LoggedInShopkeeper
import com.nativeapptemplate.nativeapptemplatefree.model.Permissions
import com.nativeapptemplate.nativeapptemplatefree.model.ScanState
import com.nativeapptemplate.nativeapptemplatefree.model.ShowTagInfoScanResult
import com.nativeapptemplate.nativeapptemplatefree.model.ShowTagInfoScanResultType
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
        maximumQueueNumberLength = it.maximumQueueNumberLength,
        shopLimitCount = it.shopLimitCount,

        isEmailUpdated = it.isEmailUpdated,
        isMyAccountDeleted = it.isMyAccountDeleted,

        scanViewSelectedTabIndex = it.scanViewSelectedTabIndex,
        shouldFetchItemTagForShowTagInfoScan = it.shouldFetchItemTagForShowTagInfoScan,
        shouldCompleteItemTagForCompleteScan = it.shouldCompleteItemTagForCompleteScan,
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
          this.maximumQueueNumberLength = permissions.getMaximumQueueNumberLength()!!
          this.shopLimitCount = permissions.getShopLimitCount()!!
        }
      }
    } catch (ioException: IOException) {
      Log.e("NatPreferences", "Failed to update user preferences", ioException)
      throw ioException
    }
  }

  suspend fun setShouldFetchItemTagForShowTagInfoScan(shouldFetchItemTagForShowTagInfoScan: Boolean) {
    try {
      userPreferences.updateData {
        it.copy { this.shouldFetchItemTagForShowTagInfoScan = shouldFetchItemTagForShowTagInfoScan }
      }
    } catch (ioException: IOException) {
      Log.e("MtcPreferences", "Failed to update user preferences", ioException)
      throw ioException
    }
  }

  suspend fun setShouldCompleteItemTagForCompleteScan(shouldCompleteItemTagForCompleteScan: Boolean) {
    try {
      userPreferences.updateData {
        it.copy { this.shouldCompleteItemTagForCompleteScan = shouldCompleteItemTagForCompleteScan }
      }
    } catch (ioException: IOException) {
      Log.e("MtcPreferences", "Failed to update user preferences", ioException)
      throw ioException
    }
  }

  suspend fun setShouldNavigateToScanView(shouldNavigateToScanView: Boolean) {
    try {
      userPreferences.updateData {
        it.copy { this.shouldNavigateToScanView = shouldNavigateToScanView }
      }
    } catch (ioException: IOException) {
      Log.e("MtcPreferences", "Failed to update user preferences", ioException)
      throw ioException
    }
  }

  suspend fun setScanViewSelectedTabIndex(scanViewSelectedTabIndex: Int) {
    try {
      userPreferences.updateData {
        it.copy { this.scanViewSelectedTabIndex = scanViewSelectedTabIndex }
      }
    } catch (ioException: IOException) {
      Log.e("MtcPreferences", "Failed to update user preferences", ioException)
      throw ioException
    }
  }

  suspend fun setCompleteScanResult(
    completeScanResult: CompleteScanResult,
  ) {
    val itemTagInfoFromNdefMessage = completeScanResult.itemTagInfoFromNdefMessage
    val itemTagData = completeScanResult.itemTagData
    val completeScanResultType = completeScanResult.completeScanResultType
    val message = completeScanResult.message

    try {
      val scanResultProto = setScanResult(
        itemTagInfoFromNdefMessage = itemTagInfoFromNdefMessage,
        itemTagData = itemTagData,
        scanResultType = completeScanResultType.param,
        message = message,
      )

      userPreferences.updateData {
        it.copy {
          this.completeScanResult = scanResultProto
        }
      }
    } catch (ioException: IOException) {
      Log.e("MtcPreferences", "Failed to update user preferences", ioException)
      throw ioException
    }
  }

  suspend fun setShowTagInfoScanResult(
    showTagInfoScanResult: ShowTagInfoScanResult,
  ) {
    val itemTagInfoFromNdefMessage = showTagInfoScanResult.itemTagInfoFromNdefMessage
    val itemTagData = showTagInfoScanResult.itemTagData
    val showTagInfoScanResultType = showTagInfoScanResult.showTagInfoScanResultType
    val message = showTagInfoScanResult.message

    try {
      val scanResultProto = setScanResult(
        itemTagInfoFromNdefMessage = itemTagInfoFromNdefMessage,
        itemTagData = itemTagData,
        scanResultType = showTagInfoScanResultType.param,
        message = message,
      )

      userPreferences.updateData {
        it.copy {
          this.showTagInfoScanResult = scanResultProto
        }
      }
    } catch (ioException: IOException) {
      Log.e("MtcPreferences", "Failed to update user preferences", ioException)
      throw ioException
    }
  }

  private fun setScanResult(
    itemTagInfoFromNdefMessage: ItemTagInfoFromNdefMessage,
    itemTagData: ItemTagData,
    scanResultType: String,
    message: String,
  ): ScanResultProto {
    val itemTagInfoFromNdefMessageProto = setItemTagInfoFromNdefMessage(itemTagInfoFromNdefMessage)
    val itemTagProto = setItemTagData(itemTagData)

    return ScanResultProto.newBuilder()
      .setItemTagInfoFromNdefMessage(itemTagInfoFromNdefMessageProto)
      .setItemTagData(itemTagProto)
      .setScanResultType(scanResultType)
      .setMessage(message)
      .build()
  }

  private fun setItemTagInfoFromNdefMessage(itemTagInfoFromNdefMessage: ItemTagInfoFromNdefMessage): ItemTagInfoFromNdefMessageProto {
    return ItemTagInfoFromNdefMessageProto.newBuilder()
      .setId(itemTagInfoFromNdefMessage.id)
      .setItemTagType(itemTagInfoFromNdefMessage.itemTagType.param)
      .setSuccess(itemTagInfoFromNdefMessage.success)
      .setMessage(itemTagInfoFromNdefMessage.message)
      .setIsReadOnly(itemTagInfoFromNdefMessage.isReadOnly)
      .setScannedAt(itemTagInfoFromNdefMessage.scannedAt)
      .build()
  }

  private fun setItemTagData(itemTagData: ItemTagData): ItemTagDataProto {
    return ItemTagDataProto.newBuilder()
      .setId(itemTagData.id)
      .setShopId(itemTagData.shopId)
      .setQueueNumber(itemTagData.queueNumber)
      .setState(itemTagData.state.param)
      .setScanState(itemTagData.scanState.param)
      .setCreatedAt(itemTagData.createdAt)
      .setCustomerReadAt(itemTagData.customerReadAt)
      .setCompletedAt(itemTagData.completedAt)
      .setShopName(itemTagData.shopName)
      .setAlreadyCompleted(itemTagData.alreadyCompleted)
      .build()
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

  suspend fun setDidShowTapShopBelowTip(didShowTapShopBelowTip: Boolean) {
    try {
      userPreferences.updateData {
        it.copy { this.didShowTapShopBelowTip = didShowTapShopBelowTip }
      }
    } catch (ioException: IOException) {
      Log.e("NatPreferences", "Failed to update user preferences", ioException)
      throw ioException
    }
  }

  suspend fun setDidShowReadInstructionsTip(didShowReadInstructionsTip: Boolean) {
    try {
      userPreferences.updateData {
        it.copy { this.didShowReadInstructionsTip = didShowReadInstructionsTip }
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

  fun didShowTapShopBelowTip(): Flow<Boolean> = userPreferences.data
    .map { data ->
      data.didShowTapShopBelowTip
    }

  fun didShowReadInstructionsTip(): Flow<Boolean> = userPreferences.data
    .map { data ->
      data.didShowReadInstructionsTip
    }

  fun getMaximumQueueNumberLength(): Flow<Int> = userPreferences.data
    .map { data ->
      data.maximumQueueNumberLength
    }

  fun shouldFetchItemTagForShowTagInfoScan(): Flow<Boolean> = userPreferences.data
    .map { data ->
      data.shouldFetchItemTagForShowTagInfoScan
    }

  fun shouldCompleteItemTagForCompleteScan(): Flow<Boolean> = userPreferences.data
    .map { data ->
      data.shouldCompleteItemTagForCompleteScan
    }

  fun shouldNavigateToScanView(): Flow<Boolean> = userPreferences.data
    .map { data ->
      data.shouldNavigateToScanView
    }

  fun scanViewSelectedTabIndex(): Flow<Int> = userPreferences.data
    .map { data ->
      data.scanViewSelectedTabIndex
    }

  fun completeScanResult(): Flow<CompleteScanResult> = userPreferences.data
    .map { data ->
      completeScanResultFrom(data.completeScanResult)
    }

  fun showTagInfoScanResult(): Flow<ShowTagInfoScanResult> = userPreferences.data
    .map { data ->
      showTagInfoScanResultFrom(data.showTagInfoScanResult)
    }

  private fun showTagInfoScanResultFrom(scanResultProto: ScanResultProto): ShowTagInfoScanResult {
    val itemTagInfoFromNdefMessage = itemTagInfoFromNdefMessageFrom(
      scanResultProto.itemTagInfoFromNdefMessage
    )

    val itemTagData = itemTagDataFrom(
      scanResultProto.itemTagData
    )

    return ShowTagInfoScanResult(
      itemTagInfoFromNdefMessage = itemTagInfoFromNdefMessage,
      itemTagData = itemTagData,
      showTagInfoScanResultType = ShowTagInfoScanResultType.fromParam(scanResultProto.scanResultType) ?: ShowTagInfoScanResultType.Idled,
      message = scanResultProto.message,
    )
  }

  private fun completeScanResultFrom(scanResultProto: ScanResultProto): CompleteScanResult {
    val itemTagInfoFromNdefMessage = itemTagInfoFromNdefMessageFrom(
      scanResultProto.itemTagInfoFromNdefMessage
    )

    val itemTagData = itemTagDataFrom(
      scanResultProto.itemTagData
    )

    return CompleteScanResult(
      itemTagInfoFromNdefMessage = itemTagInfoFromNdefMessage,
      itemTagData = itemTagData,
      completeScanResultType = CompleteScanResultType.fromParam(scanResultProto.scanResultType) ?: CompleteScanResultType.Idled,
      message = scanResultProto.message,
    )
  }

  private fun itemTagInfoFromNdefMessageFrom(
    itemTagInfoFromNdefMessageProto: ItemTagInfoFromNdefMessageProto
  ): ItemTagInfoFromNdefMessage {
    return ItemTagInfoFromNdefMessage(
      id = itemTagInfoFromNdefMessageProto.id,
      itemTagType = ItemTagType.fromParam(itemTagInfoFromNdefMessageProto.itemTagType)
        ?: ItemTagType.Server,
      success = itemTagInfoFromNdefMessageProto.success,
      message = itemTagInfoFromNdefMessageProto.message,
      isReadOnly = itemTagInfoFromNdefMessageProto.isReadOnly,
      scannedAt = itemTagInfoFromNdefMessageProto.scannedAt,
    )
  }

  private fun itemTagDataFrom(itemTagDataProto: ItemTagDataProto): ItemTagData {
    return ItemTagData(
      id = itemTagDataProto.id,
      shopId = itemTagDataProto.shopId,
      queueNumber = itemTagDataProto.queueNumber,
      state = ItemTagState.fromParam(itemTagDataProto.state) ?: ItemTagState.Idled,
      scanState = ScanState.fromParam(itemTagDataProto.scanState) ?: ScanState.Unscanned,
      createdAt = itemTagDataProto.createdAt,
      customerReadAt = itemTagDataProto.customerReadAt,
      completedAt = itemTagDataProto.completedAt,
      shopName = itemTagDataProto.shopName,
      alreadyCompleted = itemTagDataProto.alreadyCompleted,
    )
  }
}