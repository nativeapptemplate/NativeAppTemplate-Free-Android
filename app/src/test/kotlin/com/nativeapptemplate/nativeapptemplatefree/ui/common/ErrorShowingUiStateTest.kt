package com.nativeapptemplate.nativeapptemplatefree.ui.common

import com.nativeapptemplate.nativeapptemplatefree.testing.util.TEST_API_ERROR_MESSAGE
import com.nativeapptemplate.nativeapptemplatefree.testing.util.testApiError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ErrorShowingUiStateTest {
  private data class TestUiState(
    val value: String = "kept",
    val isLoading: Boolean = true,
    val message: String = "",
  ) : ErrorShowingUiState<TestUiState> {
    override fun showingError(message: String) = copy(message = message, isLoading = false)
  }

  @Test
  fun showError_showsTheCodedDescriptionAndKeepsTheRest() {
    val state = MutableStateFlow(TestUiState())

    state.showError(testApiError)

    // TEST_API_ERROR_MESSAGE is testApiError's codedDescription, "[NATIVEAPPTEMPLATE-2001] ...".
    assertEquals(TestUiState(value = "kept", isLoading = false, message = TEST_API_ERROR_MESSAGE), state.value)
  }

  @Test
  fun catchShowingError_showsAFailureAndEndsTheFlow() = runTest {
    val state = MutableStateFlow(TestUiState())

    val values = flow {
      emit(1)
      throw testApiError
    }.catchShowingError(state).toList()

    assertEquals(listOf(1), values)
    assertEquals(TEST_API_ERROR_MESSAGE, state.value.message)
    assertEquals(false, state.value.isLoading)
  }

  @Test
  fun catchShowingError_leavesTheStateAloneWithoutAFailure() = runTest {
    val state = MutableStateFlow(TestUiState())

    val values = flowOf(1, 2).catchShowingError(state).toList()

    assertEquals(listOf(1, 2), values)
    assertEquals(TestUiState(), state.value)
  }
}
