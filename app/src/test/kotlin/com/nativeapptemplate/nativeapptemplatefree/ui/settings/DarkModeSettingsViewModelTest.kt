package com.nativeapptemplate.nativeapptemplatefree.ui.settings

import com.nativeapptemplate.nativeapptemplatefree.model.DarkThemeConfig
import com.nativeapptemplate.nativeapptemplatefree.testing.repository.TestLoginRepository
import com.nativeapptemplate.nativeapptemplatefree.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DarkModeSettingsViewModelTest {
  @get:Rule
  val dispatcherRule = MainDispatcherRule()

  private val loginRepository = TestLoginRepository()

  private lateinit var viewModel: DarkModeSettingsViewModel

  @Before
  fun setUp() {
    viewModel = DarkModeSettingsViewModel(
      loginRepository = loginRepository,
    )
  }

  @Test
  fun stateIsInitiallyLoading() = runTest {
    assertEquals(DarkModeSettingsUiState.Loading, viewModel.darkModeSettingsUiState.value)
  }

  @Test
  fun stateIsSuccessAfterUserDataLoaded() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.darkModeSettingsUiState.collect() }

    loginRepository.setDarkThemeConfig(DarkThemeConfig.DARK)

    assertEquals(
      DarkModeSettingsUiState.Success(
        UserEditableSettings(
          darkThemeConfig = DarkThemeConfig.DARK,
        ),
      ),
      viewModel.darkModeSettingsUiState.value,
    )
  }

  @Test
  fun darkThemeConfig_whenUpdatingDarkThemeConfig_isSavedInPreference() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.darkModeSettingsUiState.collect() }

    viewModel.updateDarkThemeConfig(DarkThemeConfig.LIGHT)

    assertEquals(
      DarkModeSettingsUiState.Success(
        UserEditableSettings(
          darkThemeConfig = DarkThemeConfig.LIGHT,
        ),
      ),
      viewModel.darkModeSettingsUiState.value,
    )
  }
}