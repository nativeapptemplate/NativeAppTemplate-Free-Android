package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Whether [value], a field's text, has changed since the field was shown, so its error can wait
 * until then (iOS's `untilEdited(_:)`). A new form opened with "Name is invalid." in red before
 * anything was typed; the save button stays disabled meanwhile, so nothing is lost by waiting.
 *
 * Decided in the composition itself, not in an effect, so a field that changes and is cleared
 * before an effect would run still counts as edited.
 */
@Composable
fun rememberIsEdited(value: String): Boolean {
  val initialValue = rememberSaveable { value }
  var wasEdited by rememberSaveable { mutableStateOf(false) }
  val isEdited = wasEdited || value != initialValue
  SideEffect {
    if (isEdited) wasEdited = true
  }
  return isEdited
}
