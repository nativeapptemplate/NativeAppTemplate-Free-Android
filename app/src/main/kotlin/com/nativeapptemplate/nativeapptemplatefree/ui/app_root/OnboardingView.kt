package com.nativeapptemplate.nativeapptemplatefree.ui.app_root

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativeapptemplate.nativeapptemplatefree.NatConstants
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.ui.common.NonScaledSp.nonScaledSp

@Composable
internal fun OnboardingView(
  onStartClick: () -> Unit,
) {
  val fontSizeLarge = 24
  val lineHeightLarge = 26
  val pagerState = rememberPagerState(pageCount = {
    3
  })

  Scaffold(
    topBar = { TopAppBar(onStartClick) },
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
    ) {
      HorizontalPager(
        state = pagerState,
        modifier = Modifier
          .fillMaxSize()
      ) { page ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .padding(top = 12.dp)
        ) {
          Image(
            painter = painterResource(OnboardingViewModel.onboardingImageId(page)),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
              .align(Alignment.TopCenter)
          )
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),

            modifier = Modifier
              .align(Alignment.BottomCenter)
          ) {
            Text(
              stringResource(OnboardingViewModel.onboardingDescription(page)),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = fontSizeLarge.sp.nonScaledSp,
              lineHeight = lineHeightLarge.sp.nonScaledSp,
              modifier = Modifier
                .padding(24.dp)
                .padding(bottom = 16.dp)
            )
          }
        }
      }
      Row(
        Modifier
          .wrapContentHeight()
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center
      ) {
        repeat(pagerState.pageCount) { iteration ->
          val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
          Box(
            modifier = Modifier
              .padding(2.dp)
              .clip(CircleShape)
              .background(color)
              .size(16.dp)
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
  onStartClick: () -> Unit,
) {
  val context = LocalContext.current

  CenterAlignedTopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      titleContentColor = MaterialTheme.colorScheme.primary,
    ),
    title = { Text("") },
    actions = {
      TextButton(
        onClick = { onStartClick() },
      ) {
        Text(
          "Start",
          style = MaterialTheme.typography.displaySmall
        )
      }
    },
    navigationIcon = {
      TextButton(
        onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(NatConstants.SUPPORT_WEBSITE_URL))) },
      ) {
        Text(
          stringResource(R.string.support_website),
        )
      }
    },

    modifier = Modifier.fillMaxWidth(),
  )
}
