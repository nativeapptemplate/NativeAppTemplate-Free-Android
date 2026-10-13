package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Runs [load] once per screen instance.
 *
 * Unlike `LifecycleEventEffect(ON_CREATE)`, it does not run again when the Activity is recreated
 * (e.g. rotation): the retained ViewModel already holds the data, plus any unsaved edits that a
 * reload would overwrite.
 *
 * Whether it ran is kept in the screen's ViewModelStore, next to the screen's ViewModel, not in saved
 * instance state: after process death the restored saved state would say "loaded" while the new
 * ViewModel is empty, and the screen would show its spinner forever.
 */
@Composable
fun LoadOnceEffect(load: () -> Unit) {
  val loadOnceState = viewModel { LoadOnceState() }
  LaunchedEffect(Unit) {
    if (!loadOnceState.hasLoaded) {
      loadOnceState.hasLoaded = true
      load()
    }
  }
}

/** Lives as long as the screen's ViewModel: through configuration changes, not process death. */
internal class LoadOnceState : ViewModel() {
  var hasLoaded = false
}
