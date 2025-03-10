package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_detail

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
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
import androidx.compose.material.icons.outlined.CrisisAlert
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.nativeapptemplate.nativeapptemplatefree.BuildConfig
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.ui.common.MainButtonView
import com.nativeapptemplate.nativeapptemplatefree.utils.Utility.getActivity
import java.io.IOException

@Composable
internal fun ItemTagWriteView(
  viewModel: ItemTagWriteViewModel = hiltViewModel(),
  onBackClick: () -> Unit,
) {
  val uiState: ItemTagWriteUiState by viewModel.uiState.collectAsStateWithLifecycle()

  val context = LocalContext.current
  val activity = context.getActivity()
  val nfcAdapter = NfcAdapter.getDefaultAdapter(context)

  val holdYourAndroidNearTheItemMessage = stringResource(R.string.hold_your_android_near_the_item)
  val tagIsNotWritableMessage = stringResource(R.string.tag_is_not_writable)
  val tagCannotBeMadeReadOnlyMessage = stringResource(R.string.tag_cannot_be_made_read_only)
  val updateSuccessMessage = stringResource(R.string.update_success)

  LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    viewModel.updateMessage(holdYourAndroidNearTheItemMessage)
  }

  DisposableEffect(nfcAdapter) {
    val nfcCallback = object : NfcAdapter.ReaderCallback {
      override fun onTagDiscovered(tag: Tag?) {
        if (tag != null) {
          val mNdef = Ndef.get(tag)
          if (mNdef != null) {
            val mRecord = NdefRecord.createUri(viewModel.scanUri)

            val mNdefMsg = if (viewModel.itemTagType == "server") {
              NdefMessage(
                arrayOf(
                  mRecord,
                  NdefRecord.createApplicationRecord(BuildConfig.APPLICATION_ID)
                )
              )
            } else {
              NdefMessage(mRecord)
            }

            try {
              mNdef.connect()

              if (!mNdef.isWritable) {
                viewModel.updateMessage(tagIsNotWritableMessage)
                viewModel.updateIsFailed(true)
                mNdef.close()
                return
              }

              mNdef.writeNdefMessage(mNdefMsg)

              if (viewModel.isLock) {
                if (!mNdef.canMakeReadOnly()) {
                  viewModel.updateMessage(tagCannotBeMadeReadOnlyMessage)
                  mNdef.close()
                  return
                }
                mNdef.makeReadOnly()
              }

              viewModel.updateMessage(updateSuccessMessage)
              viewModel.updateIsUpdated(true)
            } catch (e: Exception) {
              viewModel.updateIsFailed(true)
              viewModel.updateMessage("Update tag failed. Please try again(${e.message})")
            } finally {
              try {
                mNdef.close()
              } catch (_: IOException) {
              }
            }
          } else {
            viewModel.updateIsFailed(true)
            viewModel.updateMessage("Invalid Tag")
          }
        }
      }
    }
    nfcAdapter.enableReaderMode(
      activity,
      nfcCallback,
      NfcAdapter.FLAG_READER_NFC_A,
      null
    )

    onDispose {
      nfcAdapter.disableReaderMode(activity)
    }
  }

  ItemTagWriteView(
    uiState = uiState,
    onBackClick = onBackClick,
  )
}

@Composable
fun ItemTagWriteView(
  uiState: ItemTagWriteUiState,
  onBackClick: () -> Unit,
) {
  ContentView(
    uiState = uiState,
    onBackClick = onBackClick,
  )
}

@Composable
private fun ContentView(
  uiState: ItemTagWriteUiState,
  onBackClick: () -> Unit,
) {
  ItemTagWriteContentView(
    uiState = uiState,
    onBackClick = onBackClick,
  )
}

@Composable
private fun ItemTagWriteContentView(
  uiState: ItemTagWriteUiState,
  onBackClick: () -> Unit,
) {
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
              uiState.message,
              style = MaterialTheme.typography.titleMedium,
              textAlign = TextAlign.Center,
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
            )

            if (uiState.isUpdated) {
              Icon(
                Icons.Outlined.Done,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(128.dp)
              )
            } else if (uiState.isFailed) {
              Icon(
                Icons.Outlined.CrisisAlert,
                contentDescription = null,
                tint = Color.Red,
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
