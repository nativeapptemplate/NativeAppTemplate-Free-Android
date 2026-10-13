package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * A screen's ViewModel lives in its back-stack entry's ViewModelStore: kept across a configuration
 * change, gone after process death, while saved instance state survives both. Each case below
 * restores the saved state; only the ViewModelStore differs.
 */
@RunWith(RobolectricTestRunner::class)
class LoadOnceEffectTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  private class TestViewModelStoreOwner : ViewModelStoreOwner {
    override val viewModelStore = ViewModelStore()
  }

  private var owner by mutableStateOf<ViewModelStoreOwner>(TestViewModelStoreOwner())
  private var loadCount = 0
  private val restorationTester = StateRestorationTester(composeTestRule)

  private fun setContent() {
    restorationTester.setContent {
      CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
        LoadOnceEffect { loadCount++ }
      }
    }
    composeTestRule.waitForIdle()
  }

  // Rotation: the retained ViewModel already holds the data and any unsaved edits.
  @Test
  fun configurationChange_doesNotLoadAgain() {
    setContent()

    restorationTester.emulateSavedInstanceStateRestore()
    composeTestRule.waitForIdle()

    assertEquals(1, loadCount)
  }

  // Process death: the new ViewModel starts out empty (loading), so it must load, or the screen
  // shows its spinner forever.
  @Test
  fun processDeath_loadsAgainForTheNewViewModel() {
    setContent()

    composeTestRule.runOnIdle { owner = TestViewModelStoreOwner() }
    restorationTester.emulateSavedInstanceStateRestore()
    composeTestRule.waitForIdle()

    // Once for the first ViewModelStore, once for the one after the process restarted.
    assertEquals(2, loadCount)
  }
}
