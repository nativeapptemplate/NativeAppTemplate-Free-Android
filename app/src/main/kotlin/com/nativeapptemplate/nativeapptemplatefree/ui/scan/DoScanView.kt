package com.nativeapptemplate.nativeapptemplatefree.ui.scan

import android.nfc.NfcAdapter
import android.nfc.tech.Ndef
import android.os.Bundle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagInfoFromNdefMessage
import com.nativeapptemplate.nativeapptemplatefree.ui.common.MainButtonView
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility.getActivity
import kotlinx.coroutines.delay
import java.util.Date

@Composable
internal fun DoScanView(
  viewModel: DoScanViewModel = hiltViewModel(),
  onBackClick: () -> Unit,
) {
  val uiState: DoScanUiState by viewModel.uiState.collectAsStateWithLifecycle()

  val context = LocalContext.current
  val activity = context.getActivity()
  val nfcAdapter = NfcAdapter.getDefaultAdapter(context)

  LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    viewModel.updateScanViewSelectedTabIndex()
    viewModel.updateExecFlagAfterScanning(false)
  }

  LaunchedEffect(uiState.isScanned) {
    if (uiState.isScanned) {
      delay(2_000)
      onBackClick()
    }
  }

  DisposableEffect(nfcAdapter) {
    val nfcCallback = NfcAdapter.ReaderCallback { tag ->
      if (tag != null) {
        val ndef = Ndef.get(tag)
        var itemTagInfoFromNdefMessage = ItemTagInfoFromNdefMessage()

        if (ndef != null) {
          try {
            ndef.connect()

            val ndefMessage = ndef.ndefMessage
            itemTagInfoFromNdefMessage = Utility.extractItemTagInfoFrom(
              context = context,
              ndefMessage = ndefMessage,
              isTest = viewModel.isTest
            )

            if (itemTagInfoFromNdefMessage.success) {
              itemTagInfoFromNdefMessage.isReadOnly = !ndef.isWritable
              itemTagInfoFromNdefMessage.scannedAt = Date().toInstant().toString()
            }
          } catch (e: Exception) {
            itemTagInfoFromNdefMessage.success = false
            itemTagInfoFromNdefMessage.message =
              "Reading tag failed. Please try again(${e.message})"
          } finally {
            try {
              ndef.close()
            } catch (e: Exception) {
              itemTagInfoFromNdefMessage.success = false
              itemTagInfoFromNdefMessage.message =
                "Reading tag failed. Please try again(${e.message})"
            }
          }

          viewModel.updateItemTagInfoFromNdefMessage(itemTagInfoFromNdefMessage)
          viewModel.updateExecFlagAfterScanning(itemTagInfoFromNdefMessage.success)
          viewModel.updateIsScanned(true)
        } else {
          itemTagInfoFromNdefMessage.success = false
          itemTagInfoFromNdefMessage.message = "Invalid tag."
          viewModel.updateItemTagInfoFromNdefMessage(itemTagInfoFromNdefMessage)
          viewModel.updateExecFlagAfterScanning(false)
          viewModel.updateIsScanned(true)
        }
      }
    }

    val options = Bundle()
    options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)

    nfcAdapter.enableReaderMode(
      activity,
      nfcCallback,
      NfcAdapter.FLAG_READER_NFC_A,
      options,
    )

    onDispose {
      nfcAdapter.disableReaderMode(activity)
    }
  }

  DoScanView(
    uiState = uiState,
    onBackClick = onBackClick,
  )
}

@Composable
fun DoScanView(
  uiState: DoScanUiState,
  onBackClick: () -> Unit,
) {
  ContentView(
    uiState = uiState,
    onBackClick = onBackClick,
  )
}

@Composable
private fun ContentView(
  uiState: DoScanUiState,
  onBackClick: () -> Unit,
) {
  DoScanContentView(
    uiState = uiState,
    onBackClick = onBackClick,
  )
}

@Composable
private fun DoScanContentView(
  uiState: DoScanUiState,
  onBackClick: () -> Unit,
) {
  val holdYourAndroidNearTheItemMessage = stringResource(R.string.hold_your_android_near_the_item)

  Scaffold(
    modifier = Modifier
      .widthIn(max = LocalConfiguration.current.screenWidthDp.dp),
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(padding)
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement
          .spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically,
          ),
        modifier = Modifier
          .padding(padding)
          .padding(horizontal = 16.dp)
          .align(Alignment.Center)
          .verticalScroll(rememberScrollState()),
      ) {
        Card(
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          ),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .padding(24.dp)
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
            Text(
              stringResource(R.string.ready_for_scanning),
              style = MaterialTheme.typography.titleLarge,
              textAlign = TextAlign.Center,
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
            )

            Text(
              holdYourAndroidNearTheItemMessage,
              style = MaterialTheme.typography.titleMedium,
              textAlign = TextAlign.Center,
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
            )

            if (uiState.isScanned) {
              Icon(
                Icons.Outlined.Done,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(128.dp)
              )
            } else {
              val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.nfc_reader))
              LottieAnimation(
                composition,
                iterations = LottieConstants.IterateForever,
              )
            }

            MainButtonView(
              title = stringResource(R.string.cancel),
              onClick = { onBackClick() },
              modifier =  Modifier
                .padding(horizontal = 12.dp, vertical = 24.dp)
            )
          }
        }
      }
    }
  }
}
