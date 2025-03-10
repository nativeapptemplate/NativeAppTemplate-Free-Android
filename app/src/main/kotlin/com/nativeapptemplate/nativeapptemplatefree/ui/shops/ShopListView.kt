package com.nativeapptemplate.nativeapptemplatefree.ui.shops

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NoLuggage
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ErrorView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.MainButtonView

@Composable
internal fun ShopListView(
  viewModel: ShopListViewModel = hiltViewModel(),
  onItemClick: (String) -> Unit,
  onAddShopClick: () -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
) {
  val uiState: ShopListUiState by viewModel.uiState.collectAsStateWithLifecycle()
  val isLoggedIn: Boolean by viewModel.isLoggedIn().collectAsStateWithLifecycle()

  LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    if (isLoggedIn) {
      viewModel.reload()
    }
  }

  LaunchedEffect(uiState.message) {
    if (uiState.message.isNotBlank()) {
      onShowSnackbar(uiState.message, "dismiss", SnackbarDuration.Indefinite)
      viewModel.snackbarMessageShown()
    }
  }

  ShopListView(
    viewModel = viewModel,
    uiState = uiState,
    onItemClick = {
      onItemClick(it)
    },
    onAddShopClick = onAddShopClick,
  )
}

@Composable
fun ShopListView(
  viewModel: ShopListViewModel,
  uiState: ShopListUiState,
  onItemClick: (String) -> Unit,
  onAddShopClick: () -> Unit,
) {
  ContentView(
    viewModel = viewModel,
    uiState = uiState,
    onItemClick = onItemClick,
    onAddShopClick = onAddShopClick,
  )
}

@Composable
private fun ContentView(
  viewModel: ShopListViewModel,
  uiState: ShopListUiState,
  onItemClick: (String) -> Unit,
  onAddShopClick: () -> Unit,
) {
  if (uiState.isLoading) {
    ShopListLoadingView()
  } else if (uiState.success) {
    ShopListContentView(
      viewModel = viewModel,
      uiState = uiState,
      onItemClick = onItemClick,
      onAddShopClick = onAddShopClick,
    )
  } else {
    ShopListErrorView(viewModel)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShopListContentView(
  viewModel: ShopListViewModel,
  uiState: ShopListUiState,
  onItemClick: (String) -> Unit,
  onAddShopClick: () -> Unit,
) {
  val leftInShopSlots = uiState.shops.getLimitCount() - uiState.shops.getCreatedShopsCount()
  val isEmpty: Boolean by viewModel.isEmpty().collectAsStateWithLifecycle()

  Scaffold(
    topBar = { TopAppBar() },
    modifier = Modifier.fillMaxSize(),
    floatingActionButton = {
      if (leftInShopSlots > 0) {
        FloatingActionButton(
          onClick = {
            onAddShopClick()
          },
        ) {
          Icon(Icons.Filled.Add, stringResource(id = R.string.add_shop))
        }
      }
    }
  ) { padding ->
    val pullToRefreshState = rememberPullToRefreshState()

    if (isEmpty) {
      NoResultsView(
        leftInShopSlots = leftInShopSlots,
        padding = padding,
        onAddShopClick = onAddShopClick
      )
    } else {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight()
          .pullToRefresh(
            state = pullToRefreshState,
            isRefreshing = uiState.isLoading,
            onRefresh = viewModel::reload,
          )
          .padding(padding)
      ) {
        LazyColumn(
          Modifier.padding(24.dp)
        ) {
          item {
            if (!uiState.didShowTapShopBelowTip) {
              TapShopBelowTip(
                stringResource(R.string.tap_shop_below),
              ) {
                viewModel.updateDidShowTapShopBelowTip(true)
              }
            }
          }

          items(
            items = uiState.shops.getDatumWithRelationships(),
            key = { it.id!! }
          ) { shop ->
            ShopListCardView(
              data = shop,
              onClick = {
                onItemClick(shop.id!!)
              },
            )

            HorizontalDivider()
          }

          item {
            Row(
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Text(
                leftInShopSlots.toString(),
                modifier = Modifier.alignByBaseline(),
                style = MaterialTheme.typography.titleLarge,
              )
              Text(
                "left in shop slots.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alignByBaseline(),
              )
            }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar() {
  CenterAlignedTopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      titleContentColor = MaterialTheme.colorScheme.primary,
    ),
    title = { Text(text = stringResource(id = R.string.shops)) },
    modifier = Modifier.fillMaxWidth(),
  )
}

@Composable
fun TapShopBelowTip(
  text: String,
  onDismiss: () -> Unit,
) {
  InputChip(
    onClick = {
      onDismiss()
    },
    label = { Text(
      text,
      style = MaterialTheme.typography.titleLarge,
      color = MaterialTheme.colorScheme.tertiary,
    ) },
    selected = false,
    avatar = {
      Icon(
        Icons.Outlined.Info,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier.size(InputChipDefaults.AvatarSize)
      )
    },
    trailingIcon = {
      Icon(
        Icons.Default.Close,
        contentDescription = null,
        Modifier.size(InputChipDefaults.AvatarSize)
      )
    },
  )
}

@Composable
private fun ShopListErrorView(
  viewModel: ShopListViewModel,
) {
  Scaffold(
    topBar = { TopAppBar() },
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
private fun NoResultsView(
  leftInShopSlots: Int,
  padding: PaddingValues,
  onAddShopClick: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .padding(padding),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(24.dp),
      modifier = Modifier.padding(16.dp)
    ) {
      if (leftInShopSlots > 0) {
        Icon(
          Icons.Outlined.Storefront,
          contentDescription = null,
          modifier = Modifier.size(128.dp)
        )

        Text(
          stringResource(R.string.add_shop_description),
          modifier = Modifier
            .padding(horizontal = 16.dp)
        )

        MainButtonView(
          title = stringResource(R.string.add_shop),
          onClick = { onAddShopClick() },
          modifier =  Modifier
            .padding(horizontal = 12.dp, vertical = 24.dp)
        )
      } else {
        Icon(
          Icons.Outlined.NoLuggage,
          contentDescription = null,
          modifier = Modifier.size(128.dp)
        )

        Column(
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier
            .padding(horizontal = 16.dp)
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Text(
              leftInShopSlots.toString(),
              modifier = Modifier.alignByBaseline(),
              style = MaterialTheme.typography.titleLarge,
            )
            Text(
              "left in shop slots.",
              modifier = Modifier.alignByBaseline(),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ShopListLoadingView(
) {
  Scaffold(
    topBar = { TopAppBar() },
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
