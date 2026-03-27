package com.nativeapptemplate.nativeapptemplatefree.di.modules

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CryptoModule {

  private const val KEYSET_NAME = "nat_datastore_keyset"
  private const val PREFERENCE_FILE = "nat_datastore_key_preference"
  private const val MASTER_KEY_URI = "android-keystore://nat_datastore_master_key"

  @Provides
  @Singleton
  fun providesAead(@ApplicationContext context: Context): Aead {
    AeadConfig.register()
    return AndroidKeysetManager.Builder()
      .withSharedPref(context, KEYSET_NAME, PREFERENCE_FILE)
      .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
      .withMasterKeyUri(MASTER_KEY_URI)
      .build()
      .keysetHandle
      .getPrimitive(Aead::class.java)
  }
}
