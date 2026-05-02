package com.nativeapptemplate.nativeapptemplatefree.di.modules

import com.nativeapptemplate.nativeapptemplatefree.network.Dispatcher
import com.nativeapptemplate.nativeapptemplatefree.network.NativeAppTemplateDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
  @Provides
  @Dispatcher(NativeAppTemplateDispatchers.IO)
  fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

  @Provides
  @Dispatcher(NativeAppTemplateDispatchers.Default)
  fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
