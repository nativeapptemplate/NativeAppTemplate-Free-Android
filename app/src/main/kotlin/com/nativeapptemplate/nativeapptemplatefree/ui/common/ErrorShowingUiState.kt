package com.nativeapptemplate.nativeapptemplatefree.ui.common

import com.nativeapptemplate.nativeapptemplatefree.common.errors.codedDescription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update

/**
 * A screen's UI state that shows a failed load or save: the error in the snackbar, and the
 * spinner stopped. Implementing it once per state keeps every failure path of the screen the same,
 * so none of them can forget to stop the spinner.
 *
 * A new attempt (a load, a reload from the error view, pull to refresh or resume, a sign-in) sets
 * the message back to "" when it starts: the last attempt's error is stale, and clearing it closes
 * its snackbar (`SnackbarMessageEffect` cancels the shown one). The new attempt's own error still
 * shows.
 */
interface ErrorShowingUiState<S : ErrorShowingUiState<S>> {
  /** This state showing [message], with loading stopped. */
  fun showingError(message: String): S
}

/** Shows [exception]'s [codedDescription] in this screen state. */
fun <S : ErrorShowingUiState<S>> MutableStateFlow<S>.showError(exception: Throwable) {
  val message = exception.codedDescription
  update { it.showingError(message) }
}

/** Catches a failure of this flow and shows it in [state], ending the flow. */
fun <T, S : ErrorShowingUiState<S>> Flow<T>.catchShowingError(state: MutableStateFlow<S>): Flow<T> = catch { exception -> state.showError(exception) }
