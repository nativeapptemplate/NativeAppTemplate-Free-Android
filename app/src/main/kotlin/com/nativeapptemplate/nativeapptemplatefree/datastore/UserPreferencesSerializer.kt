/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nativeapptemplate.nativeapptemplatefree.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.crypto.tink.Aead
import com.google.protobuf.InvalidProtocolBufferException
import com.nativeapptemplate.nativeapptemplatefree.UserPreferences
import java.io.InputStream
import java.io.OutputStream
import java.security.GeneralSecurityException
import javax.inject.Inject

/**
 * An [androidx.datastore.core.Serializer] for the [UserPreferences] proto.
 *
 * Encrypts data at rest using Tink AEAD.
 */
class UserPreferencesSerializer @Inject constructor(
  private val aead: Aead,
) : Serializer<UserPreferences> {
  override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): UserPreferences {
    val bytes = input.readBytes()
    if (bytes.isEmpty()) return defaultValue
    return try {
      val decrypted = aead.decrypt(bytes, null)
      UserPreferences.parseFrom(decrypted)
    } catch (e: GeneralSecurityException) {
      // Only what this app encrypted is a session. The file has never been written unencrypted, so
      // bytes the key does not accept are corruption (DataStoreModule then starts over signed out),
      // never a fallback to reading them as plain proto: that would let an unencrypted file put on
      // the device, or ciphertext that happens to parse once the Keystore key is gone, stand in for
      // the signed-in shopkeeper.
      throw CorruptionException("Cannot decrypt proto.", e)
    } catch (e: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", e)
    }
  }

  override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
    val encrypted = aead.encrypt(t.toByteArray(), null)
    output.write(encrypted)
  }
}
