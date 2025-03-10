package com.nativeapptemplate.nativeapptemplatefree.ui.scan

import com.nativeapptemplate.nativeapptemplatefree.model.Attributes
import com.nativeapptemplate.nativeapptemplatefree.model.CompleteScanResultType
import com.nativeapptemplate.nativeapptemplatefree.model.Data
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTag
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagData
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagInfoFromNdefMessage
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagType
import com.nativeapptemplate.nativeapptemplatefree.model.ShowTagInfoScanResultType
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestItemTagRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestLoginRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.emptyUserData
import com.nativeapptemplate.nativeapptemplatefree.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ScanViewModelTest {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val loginRepository = TestLoginRepository()
  private val itemTagRepository = TestItemTagRepository()

  private lateinit var viewModel: ScanViewModel

  @Before
  fun setUp() {
    viewModel = ScanViewModel(
      loginRepository = loginRepository,
      itemTagRepository = itemTagRepository,
    )
  }

  @Test
  fun stateIsInitiallyLoading() = runTest {
    assertTrue(viewModel.uiState.value.isLoading)
  }

  @Test
  fun stateUserData_whenSuccess_matchesUserDataFromRepository() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    loginRepository.sendUserData(emptyUserData)

    viewModel.reload()
    val uiStateValue = viewModel.uiState.value
    assertTrue(uiStateValue.success)
    assertFalse(uiStateValue.isLoading)

    assertEquals(emptyUserData, uiStateValue.userData)
  }

  @Test
  fun itemTag_whenFetchingWithItemTagInfoFromNdefMessage_isSetInPreference() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    loginRepository.sendUserData(emptyUserData)
    itemTagRepository.sendItemTag(testInputItemTag)

    viewModel.reload()
    viewModel.fetchItemTagForShowTagInfoScan(testInputItemTagInfoFromNdefMessage)

    val uiStateValue = viewModel.uiState.value
    val showTagInfoScanResult = uiStateValue.showTagInfoScanResult
    assertEquals(
      showTagInfoScanResult.itemTagInfoFromNdefMessage,
      testInputItemTagInfoFromNdefMessage
    )

    assertEquals(
      showTagInfoScanResult.itemTagData,
      ItemTagData(testInputItemTag)
    )

    assertEquals(
      showTagInfoScanResult.showTagInfoScanResultType,
      ShowTagInfoScanResultType.Succeeded
    )
  }

  @Test
  fun itemTag_whenCompletingWithItemTagInfoFromNdefMessage_isSetInPreference() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    loginRepository.sendUserData(emptyUserData)
    itemTagRepository.sendItemTag(testInputItemTag)

    viewModel.reload()
    viewModel.completeItemTag(testInputItemTagInfoFromNdefMessage)

    val uiStateValue = viewModel.uiState.value
    val completeScanResult = uiStateValue.completeScanResult
    assertEquals(
      completeScanResult.itemTagInfoFromNdefMessage,
      testInputItemTagInfoFromNdefMessage
    )

    assertEquals(
      completeScanResult.itemTagData,
      ItemTagData(testInputItemTag)
    )

    assertEquals(
      completeScanResult.completeScanResultType,
      CompleteScanResultType.Completed
    )

    assertEquals(
      uiStateValue.isAlreadyCompleted,
      false
    )
  }

  @Test
  fun stateIsAlreadyCompleted_whenCompletingAlreadyCompletedItemTag_becomesTrue() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    loginRepository.sendUserData(emptyUserData)

    val newTestInputItemTag = ItemTag(
      datum = Data(
        id = ITEM_TAG_ID,
        type = ITEM_TAG_TYPE,
        attributes = testInputItemTag.datum!!.attributes!!.copy(
          alreadyCompleted = true,
        )
      )
    )

    itemTagRepository.sendItemTag(newTestInputItemTag)

    viewModel.reload()
    viewModel.completeItemTag(testInputItemTagInfoFromNdefMessage)

    val uiStateValue = viewModel.uiState.value
    val completeScanResult = uiStateValue.completeScanResult
    assertEquals(
      completeScanResult.itemTagInfoFromNdefMessage,
      testInputItemTagInfoFromNdefMessage
    )

    assertEquals(
      completeScanResult.itemTagData,
      ItemTagData(newTestInputItemTag)
    )

    assertEquals(
      completeScanResult.completeScanResultType,
      CompleteScanResultType.Completed
    )

    assertEquals(
      uiStateValue.isAlreadyCompleted,
      true
    )
  }

  @Test
  fun itemTag_whenResettingWithItemTagInfoFromNdefMessage_isSetInPreference() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    loginRepository.sendUserData(emptyUserData)
    itemTagRepository.sendItemTag(testInputItemTag)

    viewModel.reload()
    viewModel.resetItemTag(testInputItemTagInfoFromNdefMessage)

    val uiStateValue = viewModel.uiState.value
    val completeScanResult = uiStateValue.completeScanResult
    assertEquals(
      completeScanResult.itemTagInfoFromNdefMessage,
      testInputItemTagInfoFromNdefMessage
    )

    assertEquals(
      completeScanResult.itemTagData,
      ItemTagData(testInputItemTag)
    )

    assertEquals(
      completeScanResult.completeScanResultType,
      CompleteScanResultType.Reset
    )

    assertEquals(
      uiStateValue.isAlreadyCompleted,
      false
    )
  }

  @Test
  fun stateMessage_isUpdated() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    loginRepository.sendUserData(emptyUserData)
    itemTagRepository.sendItemTag(testInputItemTag)

    viewModel.reload()
    val newMessage = "new message"
    viewModel.updateMessage(newMessage)

    val uiStateValue = viewModel.uiState.value
    assertEquals(uiStateValue.message, newMessage)
  }

  @Test
  fun stateIsAlreadyCompleted_isUpdated() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    loginRepository.sendUserData(emptyUserData)
    itemTagRepository.sendItemTag(testInputItemTag)

    viewModel.reload()
    assertFalse(viewModel.uiState.value.isAlreadyCompleted)

    viewModel.updateIsAlreadyCompleted(true)

    val uiStateValue = viewModel.uiState.value
    assertTrue(uiStateValue.isAlreadyCompleted)
  }

  @Test
  fun shouldFetchItemTagForShowTagInfoScan_isSavedInPreference() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    loginRepository.sendUserData(emptyUserData)
    itemTagRepository.sendItemTag(testInputItemTag)

    viewModel.reload()
    assertFalse(viewModel.uiState.value.userData.shouldFetchItemTagForShowTagInfoScan)

    viewModel.updateShouldFetchItemTagForShowTagInfoScan(true)

    viewModel.reload()
    assertTrue(viewModel.uiState.value.userData.shouldFetchItemTagForShowTagInfoScan)
  }

  @Test
  fun shouldCompleteItemTagForCompleteScan_isSavedInPreference() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

    loginRepository.sendUserData(emptyUserData)
    itemTagRepository.sendItemTag(testInputItemTag)

    viewModel.reload()
    assertFalse(viewModel.uiState.value.userData.shouldCompleteItemTagForCompleteScan)

    viewModel.updateShouldCompleteItemTagForCompleteScan(true)

    viewModel.reload()
    assertTrue(viewModel.uiState.value.userData.shouldCompleteItemTagForCompleteScan)
  }
}

private const val SHOP_ID = "5712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val SHOP_NAME = "8th & Townsend"

private const val ITEM_TAG_TYPE = "item_tag"
private const val ITEM_TAG_ID = "9712F2DF-DFC7-A3AA-66BC-191203654A1A"
private const val ITEM_TAG_QUEUE_NUMBER = "A001"
private const val ITEM_TAG_STATE = "idled"
private const val ITEM_TAG_SCAN_STATE = "unscanned"
private const val ITEM_TAG_CREATED_AT = "2025-01-02T12:00:00.000Z"
private const val ITEM_TAG_CUSTOMER_READ_AT = "2025-01-02T12:00:01.000Z"
private const val ITEM_TAG_COMPLETED_AT = "2025-01-02T12:00:03.000Z"
private const val ITEM_TAG_ALREADY_COMPLETED = false

private val testInputItemTagData =
  Data(
    id = ITEM_TAG_ID,
    type = ITEM_TAG_TYPE,
    attributes = Attributes(
      shopId = SHOP_ID,
      queueNumber = ITEM_TAG_QUEUE_NUMBER,
      state = ITEM_TAG_STATE,
      scanState = ITEM_TAG_SCAN_STATE,
      createdAt = ITEM_TAG_CREATED_AT,
      shopName = SHOP_NAME,
      customerReadAt = ITEM_TAG_CUSTOMER_READ_AT,
      completedAt = ITEM_TAG_COMPLETED_AT,
      alreadyCompleted = ITEM_TAG_ALREADY_COMPLETED
    )
  )

private val testInputItemTag = ItemTag(
  datum = testInputItemTagData,
)

private const val ITEM_TAG_INFO_FROM_NDEF_MESSAGE_ID = "9712F2DF-DFC7-A3AA-66BC-191203654A1A"
private val ITEM_TAG_INFO_FROM_NDEF_MESSAGE_ITEM_TAG_TYPE = ItemTagType.Server
private const val ITEM_TAG_INFO_FROM_NDEF_MESSAGE_SUCCESS = true
private const val ITEM_TAG_INFO_FROM_NDEF_MESSAGE_MESSAGE = "message"
private const val ITEM_TAG_INFO_FROM_NDEF_MESSAGE_IS_READ_ONLY = false
private const val ITEM_TAG_INFO_FROM_NDEF_MESSAGE_SCANNED_AT = ""

private val testInputItemTagInfoFromNdefMessage = ItemTagInfoFromNdefMessage(
  id = ITEM_TAG_INFO_FROM_NDEF_MESSAGE_ID,
  itemTagType = ITEM_TAG_INFO_FROM_NDEF_MESSAGE_ITEM_TAG_TYPE,
  success = ITEM_TAG_INFO_FROM_NDEF_MESSAGE_SUCCESS,
  message = ITEM_TAG_INFO_FROM_NDEF_MESSAGE_MESSAGE,
  isReadOnly = ITEM_TAG_INFO_FROM_NDEF_MESSAGE_IS_READ_ONLY,
  scannedAt = ITEM_TAG_INFO_FROM_NDEF_MESSAGE_SCANNED_AT,
)
