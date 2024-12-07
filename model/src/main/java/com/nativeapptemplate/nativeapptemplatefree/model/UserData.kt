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

package com.nativeapptemplate.nativeapptemplatefree.model

/**
 * Class summarizing user interest data
 */
data class UserData(
  val id: String = "",
  val accountId: String = "",
  val personalAccountId : String = "",
  val accountOwnerId: String = "",
  val accountName: String = "",
  val email: String = "",
  val name: String = "",
  val timeZone: String = "",
  val token: String = "",
  val client: String = "",
  val uid: String = "",
  val expiry: String = "",

  val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,

  val isLoggedIn: Boolean = false,

  var androidAppVersion: Int = -1,
  var shouldUpdatePrivacy: Boolean= false,
  var shouldUpdateTerms: Boolean= false,
  var shopLimitCount: Int = -1,

  var isEmailUpdated: Boolean = false,
  var isMyAccountDeleted: Boolean = false,
)