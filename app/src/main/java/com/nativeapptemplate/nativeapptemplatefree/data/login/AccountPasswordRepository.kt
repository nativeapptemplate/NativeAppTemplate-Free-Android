package com.nativeapptemplate.nativeapptemplatefree.data.login

import com.nativeapptemplate.nativeapptemplatefree.model.*
import kotlinx.coroutines.flow.Flow

interface AccountPasswordRepository {
  fun updateAccountPassword(
    updatePasswordBody: UpdatePasswordBody
  ): Flow<Boolean>
}
