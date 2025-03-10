package com.nativeapptemplate.nativeapptemplatefree.ui.shop_settings.item_tag_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nativeapptemplate.nativeapptemplatefree.R
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ActionIcon
import com.nativeapptemplate.nativeapptemplatefree.ui.common.ErrorView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.LoadingView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.MainButtonView
import com.nativeapptemplate.nativeapptemplatefree.ui.common.SwipeableItemWithActions

@Composable
internal fun ItemTagListView(
  viewModel: ItemTagListViewModel = hiltViewModel(),
  onItemClick: (String) -> Unit,
  onAddItemTagClick: (String) -> Unit,
  onShowSnackbar: suspend (String, String?, SnackbarDuration?) -> Boolean,
  onBackClick: () -> Unit,
) {
  val uiState: ItemTagListUiState by viewModel.uiState.collectAsStateWithLifecycle()

  LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    viewModel.reload()
  }

  LaunchedEffect(uiState.message) {
    if (uiState.message.isNotBlank()) {
      onShowSnackbar(uiState.message, "dismiss", SnackbarDuration.Indefinite)
      viewModel.snackbarMessageShown()
    }
  }

  ItemTagListView(
    viewModel = viewModel,
    uiState = uiState,
    onItemClick = onItemClick,
    onAddItemTagClick = onAddItemTagClick,
    onBackClick = onBackClick,
  )
}

@Composable
fun ItemTagListView(
  viewModel: ItemTagListViewModel,
  uiState: ItemTagListUiState,
  onItemClick: (String) -> Unit,
  onAddItemTagClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  ContentView(
    viewModel = viewModel,
    uiState = uiState,
    onItemClick = onItemClick,
    onAddItemTagClick = onAddItemTagClick,
    onBackClick = onBackClick,
  )
}

@Composable
private fun ContentView(
  viewModel: ItemTagListViewModel,
  uiState: ItemTagListUiState,
  onItemClick: (String) -> Unit,
  onAddItemTagClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  if (uiState.isLoading) {
    ItemTagListLoadingView( onBackClick)
  } else if (uiState.success) {
    ItemTagListContentView(
      viewModel = viewModel,
      uiState = uiState,
      onItemClick = onItemClick,
      onAddItemTagClick = onAddItemTagClick,
      onBackClick = onBackClick,
    )
  } else {
    ItemTagListErrorView(
      viewModel = viewModel,
      onBackClick = onBackClick,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemTagListContentView(
  viewModel: ItemTagListViewModel,
  uiState: ItemTagListUiState,
  onItemClick: (String) -> Unit,
  onAddItemTagClick: (String) -> Unit,
  onBackClick: () -> Unit,
) {
  val isEmpty: Boolean by viewModel.isEmpty().collectAsStateWithLifecycle()
  val itemTags = uiState.itemTags.getDatumWithRelationships().toMutableList()

  Scaffold(
    topBar = {
      TopAppBar(
        onBackClick,
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = {
          onAddItemTagClick(viewModel.shopId)
        },
      ) {
        Icon(Icons.Filled.Add, stringResource(id = R.string.add_shop))
      }
    },
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    val pullToRefreshState = rememberPullToRefreshState()

    if (isEmpty) {
      NoResultsView(
        viewModel = viewModel,
        onAddItemTagClick = onAddItemTagClick,
        padding = padding,
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
            Text(
              uiState.shop.getName(),
              style = MaterialTheme.typography.titleLarge,
              textAlign = TextAlign.Center,
              modifier = Modifier
                .fillMaxWidth()
            )
          }
          itemsIndexed(
            items = itemTags,
          ) { index, itemTag ->
            SwipeableItemWithActions(
              isRevealed = itemTag.isOptionsRevealed,
              onExpanded = {
                itemTags[index] = itemTag.copy(isOptionsRevealed = true)
              },
              onCollapsed = {
                itemTags[index] = itemTag.copy(isOptionsRevealed = false)
              },
              actions = {
                ActionIcon(
                  onClick = {
                    viewModel.deleteItemTag(itemTag.id!!)
                  },
                  backgroundColor = Color.Red,
                  icon = Icons.Default.Delete,
                  modifier = Modifier.fillMaxHeight()
                )
              },
            ) {
              ItemTagListCardView(
                data = itemTag,
                onItemClick = { onItemClick(itemTag.id!!) },
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
  onBackClick: () -> Unit,
) {
  CenterAlignedTopAppBar(
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      titleContentColor = MaterialTheme.colorScheme.primary,
    ),
    title = { Text(stringResource(R.string.label_shop_settings_manage_number_tags)) },
    navigationIcon = {
      IconButton(onClick = {
        onBackClick()
      }) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
      }
    },
    modifier = Modifier.fillMaxWidth(),
  )
}

@Composable
private fun NoResultsView(
  viewModel: ItemTagListViewModel,
  onAddItemTagClick: (String) -> Unit,
  padding: PaddingValues,
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
      Icon(
        Icons.Outlined.Rectangle,
        contentDescription = null,
        modifier = Modifier.size(128.dp)
      )

      Text(
        stringResource(R.string.add_tag_description),
        modifier = Modifier
          .padding(horizontal = 16.dp)
      )

      MainButtonView(
        title = stringResource(R.string.label_add_tag),
        onClick = { onAddItemTagClick(viewModel.shopId) },
        modifier =  Modifier
          .padding(horizontal = 12.dp, vertical = 24.dp)
      )
    }
  }
}

@Composable
private fun ItemTagListErrorView(
  viewModel: ItemTagListViewModel,
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        onBackClick = onBackClick,
      )
    },
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
private fun ItemTagListLoadingView(
  onBackClick: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
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
      contentAlignment = Alignment.Center
    ) {
      LoadingView()
    }
  }
}
