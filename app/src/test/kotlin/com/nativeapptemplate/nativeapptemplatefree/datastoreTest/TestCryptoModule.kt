package com.nativeapptemplate.nativeapptemplatefree.datastoreTest

import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig
import com.nativeapptemplate.nativeapptemplatefree.di.modules.CryptoModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
  components = [SingletonComponent::class],
  replaces = [CryptoModule::class],
)
internal object TestCryptoModule {
  @Provides
  @Singleton
  fun providesAead(): Aead {
    AeadConfig.register()
    val keysetHandle = KeysetHandle.generateNew(KeyTemplates.get("AES256_GCM"))
    return keysetHandle.getPrimitive(Aead::class.java)
  }
}
