package com.nativeapptemplate.nativeapptemplatefree.demo

import android.content.Context
import java.io.InputStream

fun interface DemoAssetManager {
  fun open(context : Context, fileName: String): InputStream
}
