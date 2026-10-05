package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Runs [load] once per screen instance.
 *
 * Unlike `LifecycleEventEffect(ON_CREATE)`, it does not run again when the Activity is recreated
 * (e.g. rotation): the retained ViewModel already holds the data, plus any unsaved edits that a
 * reload would overwrite.
 */
@Composable
fun LoadOnceEffect(load: () -> Unit) {
  var hasLoaded by rememberSaveable { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    if (!hasLoaded) {
      hasLoaded = true
      load()
    }
  }
}
