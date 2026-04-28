package com.nativeapptemplate.nativeapptemplatefree.ui.shop_detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.model.ItemTagState.*
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ActionText
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ErrorView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.SnackbarMessageEffect
import com.nativeapptemplate.nativeapptemplatefree.ui.common.SwipeableItemWithActions

@Composable
internal fun ShopDetailView(
  viewModel: ShopDetailViewModel = hiltViewModel(),
  onSettingsClick: (String) -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  val uiState: ShopDetailUiState by viewModel.uiState.collectAsStateWithLifecycle()

  LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    viewModel.reload()
  }

  SnackbarMessageEffect(
    message = uiState.message,
    onShowSnackbar = onShowSnackbar,
    onMessageShown = viewModel::snackbarMessageShown,
  )

  ShopDetailView(
    viewModel = viewModel,
    uiState = uiState,
    onSettingsClick = onSettingsClick,
    onBackClick = onBackClick,
  )
}

@Composable
fun ShopDetailView(
  viewModel: ShopDetailViewModel,
  uiState: ShopDetailUiState,
  onSettingsClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  ContentView(
    viewModel = viewModel,
    uiState = uiState,
    onSettingsClick = onSettingsClick,
    onBackClick = onBackClick,
  )
}

@Composable
private fun ContentView(
  viewModel: ShopDetailViewModel,
  uiState: ShopDetailUiState,
  onSettingsClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  if (uiState.isLoading) {
    ShopDetailLoadingView(uiState, onSettingsClick, onBackClick)
  } else if (uiState.success) {
    ShopDetailContentView(
      viewModel = viewModel,
      uiState = uiState,
      onSettingsClick = onSettingsClick,
      onBackClick = onBackClick,
    )
  } else {
    ShopDetailErrorView(viewModel, uiState, onSettingsClick, onBackClick)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShopDetailContentView(
  viewModel: ShopDetailViewModel,
  uiState: ShopDetailUiState,
  onSettingsClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        uiState,
        onSettingsClick,
        onBackClick,
      )
    },
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    val pullToRefreshState = rememberPullToRefreshState()

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .pullToRefresh(
          state = pullToRefreshState,
          isRefreshing = uiState.isLoading,
          onRefresh = viewModel::reload,
        )
        .padding(padding),
    ) {
      val itemTags = uiState.itemTags.getDatumWithRelationships().toMutableList()

      LazyColumn(
        Modifier.padding(24.dp),
      ) {
        item {
          Text(
            text = stringResource(R.string.shop_detail_instruction),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = 16.dp, bottom = 8.dp),
          )
        }
        itemsIndexed(
          items = itemTags,
        ) { index, itemTag ->
          val itemTagState = itemTag.getItemTagState()

          SwipeableItemWithActions(
            isRevealed = itemTag.isOptionsRevealed,
            onExpanded = {
              itemTags[index] = itemTag.copy(isOptionsRevealed = true)
            },
            onCollapsed = {
              itemTags[index] = itemTag.copy(isOptionsRevealed = false)
            },
            actions = {
              if (itemTagState == Idled) {
                ActionText(
                  onClick = {
                    viewModel.completeItemTag(itemTag.id!!)
                  },
                  backgroundColor = Color.Blue,
                  text = "Complete",
                  modifier = Modifier
                    .fillMaxHeight()
                    .width(96.dp),
                )
              } else {
                ActionText(
                  onClick = {
                    viewModel.idleItemTag(itemTag.id!!)
                  },
                  backgroundColor = Color.Red,
                  text = "Idle",
                  modifier = Modifier
                    .fillMaxHeight()
                    .width(96.dp),
                )
              }
            },
          ) {
            ShopDetailCardView(
              data = itemTag,
            )
          }

          HorizontalDivider()
        }
      }
      Indicator(
        modifier = Modifier.align(Alignment.TopCenter),
        isRefreshing = uiState.isLoading,
        state = pullToRefreshState,
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
  uiState: ShopDetailUiState,
  onSettingsClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  CenterAlignedTopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      titleContentColor = MaterialTheme.colorScheme.primary,
    ),
    title = { Text(text = uiState.shop.getName()) },
    navigationIcon = {
      IconButton(onClick = {
        onBackClick()
      }) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
      }
    },
    actions = {
      IconButton(
        onClick = {
          if (uiState.success) {
            uiState.shop.getData()?.id?.let { onSettingsClick(it) }
          }
        },
      ) {
        Icon(
          Icons.Filled.Settings,
          "Shop Settings",
          tint = MaterialTheme.colorScheme.onSurface,
        )
      }
    },
    modifier = Modifier.fillMaxWidth(),
  )
}

@Composable
private fun ShopDetailErrorView(
  viewModel: ShopDetailViewModel,
  uiState: ShopDetailUiState,
  onSettingsClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        uiState,
        onSettingsClick,
        onBackClick,
      )
    },
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(padding),
      contentAlignment = Alignment.Center,
    ) {
      ErrorView(onClick = viewModel::reload)
    }
  }
}

@Composable
private fun ShopDetailLoadingView(
  uiState: ShopDetailUiState,
  onSettingsClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        uiState,
        onSettingsClick,
        onBackClick,
      )
    },
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(padding),
      contentAlignment = Alignment.Center,
    ) {
      LoadingView()
    }
  }
}
