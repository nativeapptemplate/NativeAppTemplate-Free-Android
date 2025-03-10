package com.nativeapptemplate.nativeapptemplatefree

object NatConstants {
  const val SCAN_PATH: String = "scan"
  const val SCAN_PATH_CUSTOMER: String = "scan_customer"

  const val SUPPORT_MAIL: String = "support@nativeapptemplate.com"
  const val HOW_TO_USE_URL: String = "https://myturntag.com/how"
  const val SUPPORT_WEBSITE_URL: String = "https://nativeapptemplate.com"
  const val FAQS_URL: String = "https://nativeapptemplate.com/faqs"
  const val DISCUSSIONS_URL: String = "https://github.com/nativeapptemplate/NativeAppTemplate-Free-Android/discussions"
  const val PRIVACY_POLICY_URL: String = "https://nativeapptemplate.com/privacy"
  const val TERMS_OF_USE_URL: String = "https://nativeapptemplate.com/terms"

  const val MINIMUM_PASSWORD_LENGTH: Int = 8

  const val PLACEHOLDER_FULLNAME: String = "John Smith"
  const val PLACEHOLDER_EMAIL: String = "you@example.com"
  const val PLACEHOLDER_PASSWORD: String = "password"

  fun baseUrlString(): String {
    val result = if (BuildConfig.PORT.isEmpty()) {
      "${BuildConfig.SCHEME}://${BuildConfig.DOMAIN}"
    } else {
      "${BuildConfig.SCHEME}://${BuildConfig.DOMAIN}:${BuildConfig.PORT}"
    }

    return result
  }
}