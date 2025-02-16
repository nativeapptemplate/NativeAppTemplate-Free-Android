package com.nativeapptemplate.nativeapptemplatefree.testing.repository

import com.nativeapptemplate.nativeapptemplatefree.data.login.AccountPasswordRepository
import com.nativeapptemplate.nativeapptemplatefree.model.UpdatePasswordBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TestAccountPasswordRepository : AccountPasswordRepository {
  override fun updateAccountPassword(
    updatePasswordBody: UpdatePasswordBody
  ): Flow<Boolean> = MutableStateFlow(true)
}