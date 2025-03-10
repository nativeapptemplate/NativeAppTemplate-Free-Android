package com.nativeapptemplate.nativeapptemplatefree.ui.scan

import android.nfc.NfcAdapter
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ErrorView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.MainButtonView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NonScaledSp.nonScaledSp
import kotlinx.coroutines.launch

enum class ScanPage(
  @StringRes val titleResId: Int,
) {
  COMPLETE_SCAN(R.string.complete_scan),
  SHOW_TAG_INFO_SCAN(R.string.show_tag_info_scan)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ScanView(
  viewModel: ScanViewModel = hiltViewModel(),
  onShowDoScanViewClick: (Boolean) -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  pages: Array<ScanPage> = ScanPage.entries.toTypedArray(),
) {
  val uiState: ScanUiState by viewModel.uiState.collectAsStateWithLifecycle()
  val sheetState = rememberModalBottomSheetState()

  LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    viewModel.reload()
  }

  LaunchedEffect(uiState.message) {
    if (uiState.message.isNotBlank()) {
      onShowSnackbar(uiState.message, "dismiss", SnackbarDuration.Indefinite)
      viewModel.snackbarMessageShown()
    }
  }

  LaunchedEffect(uiState.userData.shouldFetchItemTagForShowTagInfoScan) {
    if (uiState.userData.shouldFetchItemTagForShowTagInfoScan) {
      viewModel.fetchItemTagForShowTagInfoScan(uiState.showTagInfoScanResult.itemTagInfoFromNdefMessage)
      viewModel.updateShouldFetchItemTagForShowTagInfoScan(false)
    }
  }

  LaunchedEffect(uiState.userData.shouldCompleteItemTagForCompleteScan) {
    if (uiState.userData.shouldCompleteItemTagForCompleteScan) {
      viewModel.completeItemTag(uiState.completeScanResult.itemTagInfoFromNdefMessage)
      viewModel.updateShouldCompleteItemTagForCompleteScan(false)
    }
  }

  if (uiState.isAlreadyCompleted) {
    ModalBottomSheet(
      onDismissRequest = {
        viewModel.updateIsAlreadyCompleted(false)
      },
      sheetState = sheetState
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement
          .spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically,
          ),
        modifier = Modifier
          .padding(24.dp)
      ) {
        Text(stringResource(R.string.are_you_sure))

        MainButtonView(
          title = stringResource(R.string.reset),
          onClick = {
            viewModel.resetItemTag(uiState.completeScanResult.itemTagInfoFromNdefMessage)
          },
          color =  MaterialTheme.colorScheme.onErrorContainer,
          titleColor =  MaterialTheme.colorScheme.onErrorContainer,
          modifier =  Modifier
            .padding(horizontal = 24.dp)
        )
      }
    }
  }

  ScanView(
    viewModel = viewModel,
    uiState = uiState,
    onShowDoScanViewClick = onShowDoScanViewClick,
    pages = pages,
  )
}

@Composable
fun ScanView(
  viewModel: ScanViewModel,
  uiState: ScanUiState,
  onShowDoScanViewClick: (Boolean) -> Unit,
  pages: Array<ScanPage> = ScanPage.entries.toTypedArray(),
) {
  ContentView(
    viewModel = viewModel,
    uiState = uiState,
    onShowDoScanViewClick = onShowDoScanViewClick,
    pages = pages,
  )
}

@Composable
private fun ContentView(
  viewModel: ScanViewModel,
  uiState: ScanUiState,
  onShowDoScanViewClick: (Boolean) -> Unit,
  pages: Array<ScanPage> = ScanPage.entries.toTypedArray(),
) {
  if (uiState.isLoading) {
    ScanLoadingView()
  } else if (uiState.success) {
    ScanContentView(
      viewModel = viewModel,
      uiState = uiState,
      onShowDoScanViewClick = onShowDoScanViewClick,
      pages = pages,
    )
  } else {
    ScanErrorView(viewModel)
  }
}

@Composable
private fun ScanContentView(
  viewModel: ScanViewModel,
  uiState: ScanUiState,
  onShowDoScanViewClick: (Boolean) -> Unit,
  pages: Array<ScanPage> = ScanPage.entries.toTypedArray(),
) {
  val fontSizeMedium = 16
  val fontSizeLarge = 20
  val lineHeightMedium = 20
  val lineHeightLarge = 24
  val initialPage = uiState.userData.scanViewSelectedTabIndex
  val pagerState = rememberPagerState(pageCount = { pages.size }, initialPage = initialPage)
  val context = LocalContext.current
  val doesDeviceSupportTagScanning = NfcAdapter.getDefaultAdapter(context) != null
  val deviceDoesNotSupportTagScanningMessage = stringResource(R.string.this_device_does_not_support_tag_scanning)

  Scaffold { padding ->
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(padding),
    ) {
      val coroutineScope = rememberCoroutineScope()

      // Tab Row
      TabRow(
        selectedTabIndex = pagerState.currentPage
      ) {
        pages.forEachIndexed { index, page ->
          val title = stringResource(id = page.titleResId)
          Tab(
            selected = pagerState.currentPage == index,
            onClick = {
              coroutineScope.launch { pagerState.animateScrollToPage(index) }
            },
            text = {
              Text(
                title,
                fontSize = fontSizeMedium.sp.nonScaledSp,
                lineHeight = lineHeightMedium.sp.nonScaledSp,
              )
            },
            unselectedContentColor = MaterialTheme.colorScheme.secondary
          )
        }
      }

      // Pages
      HorizontalPager(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        state = pagerState,
        verticalAlignment = Alignment.Top
      ) { index ->
        when (pages[index]) {
          ScanPage.COMPLETE_SCAN -> {
            val scrollStateForCompleteScan = rememberScrollState()

            Column(
              modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(scrollStateForCompleteScan),
            ) {
              if (!uiState.isAlreadyCompleted) {
                Card(
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                  Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier
                      .padding(24.dp)
                  ) {
                    Row(
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                      verticalAlignment = Alignment.CenterVertically,
                    ) {
                      Icon(
                        Icons.Outlined.Flag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                      )

                      Text(
                        stringResource(R.string.complete_scan),
                        fontSize = fontSizeLarge.sp.nonScaledSp,
                        lineHeight = lineHeightLarge.sp.nonScaledSp,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                      )
                    }
                    MainButtonView(
                      title = stringResource(R.string.title_scan),
                      onClick = {
                        if (doesDeviceSupportTagScanning) {
                          onShowDoScanViewClick(false)
                        } else {
                          viewModel.updateMessage(deviceDoesNotSupportTagScanningMessage)
                        }
                      },
                      color =  MaterialTheme.colorScheme.onPrimary,
                      titleColor =  MaterialTheme.colorScheme.onPrimary,
                      modifier =  Modifier
                        .padding(horizontal = 24.dp)
                    )

                    Text(
                      stringResource(R.string.complete_scan_help),
                      fontSize = fontSizeMedium.sp.nonScaledSp,
                      lineHeight = lineHeightMedium.sp.nonScaledSp,
                      style = MaterialTheme.typography.titleMedium,
                      color = MaterialTheme.colorScheme.onPrimary,
                    )
                  }
                }
              }
              CompleteScanResultView(uiState.completeScanResult)
            }
          }

          ScanPage.SHOW_TAG_INFO_SCAN -> {
            val scrollStateForShowTagInfoScan = rememberScrollState()

            Column(
              modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(scrollStateForShowTagInfoScan),
            ) {
              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseSurface),
              ) {
                Column(
                  verticalArrangement = Arrangement.spacedBy(24.dp),
                  modifier = Modifier
                    .padding(24.dp)
                ) {
                  Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Icon(
                      Icons.Outlined.Info,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.inverseOnSurface,
                    )

                    Text(
                      stringResource(R.string.show_tag_info_scan),
                      fontSize = fontSizeLarge.sp.nonScaledSp,
                      lineHeight = lineHeightLarge.sp.nonScaledSp,
                      style = MaterialTheme.typography.titleMedium,
                      color = MaterialTheme.colorScheme.inverseOnSurface,
                    )
                  }
                  MainButtonView(
                    title = stringResource(R.string.title_scan),
                    onClick = {
                      if (doesDeviceSupportTagScanning) {
                        onShowDoScanViewClick(true)
                      } else {
                        viewModel.updateMessage(deviceDoesNotSupportTagScanningMessage)
                      }
                    },
                    color =  MaterialTheme.colorScheme.inverseOnSurface,
                    titleColor =  MaterialTheme.colorScheme.inverseOnSurface,
                    modifier =  Modifier
                      .padding(horizontal = 24.dp)
                  )

                  Text(
                    stringResource(R.string.show_tag_info_scan_help),
                    fontSize = fontSizeMedium.sp.nonScaledSp,
                    lineHeight = lineHeightMedium.sp.nonScaledSp,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                  )
                }
              }

              ShowTagInfoScanResultView(uiState.showTagInfoScanResult)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ScanErrorView(
  viewModel: ScanViewModel,
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(padding),
      contentAlignment = Alignment.Center
    ) {
      ErrorView(
        onClick = { viewModel.reload() }
      )
    }
  }
}

@Composable
private fun ScanLoadingView(
) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(padding),
      contentAlignment = Alignment.Center
    ) {
      LoadingView()
    }
  }
}



