package com.nativeapptemplate.nativeapptemplatefree.model
enum class ScanState(val param: String, val title: String) {
  Unscanned("unscanned", "Unscanned"),
  Scanned("scanned", "Scanned");

  companion object {
    fun fromParam(param: String?): ScanState? = param?.let { paramMap[it] }

    internal val paramMap = entries.associateBy(ScanState::param)
  }
}
