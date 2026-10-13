package com.nativeapptemplate.nativeapptemplatefree.testing.repository

import com.nativeapptemplate.nativeapptemplatefree.data.login.AppLanguageSyncer
import com.nativeapptemplate.nativeapptemplatefree.data.login.SessionLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * A [SessionLifecycle] over the test doubles. Its application scope runs on the main dispatcher,
 * which `MainDispatcherRule` replaces with the test's, and outlives any view model's scope.
 */
fun testSessionLifecycle(
  loginRepository: TestLoginRepository,
  signUpRepository: TestSignUpRepository = TestSignUpRepository(),
  appLanguageProvider: TestAppLanguageProvider = TestAppLanguageProvider("en"),
) = SessionLifecycle(
  loginRepository = loginRepository,
  appLanguageSyncer = AppLanguageSyncer(loginRepository, signUpRepository, appLanguageProvider),
  scope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
)
