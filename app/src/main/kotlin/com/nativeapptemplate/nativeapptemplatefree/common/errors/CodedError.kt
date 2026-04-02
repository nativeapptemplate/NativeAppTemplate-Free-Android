package com.nativeapptemplate.nativeapptemplatefree.common.errors

interface CodedError {
  val errorCode: String
  val errorDescription: String
  val formattedDescription: String
    get() = "[$errorCode] $errorDescription"
}

val Throwable.codedDescription: String
  get() = (this as? CodedError)?.formattedDescription ?: message ?: "Unknown Error"
