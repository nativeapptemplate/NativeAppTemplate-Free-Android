package com.nativeapptemplate.nativeapptemplatefree.model

enum class ItemTagState(val param: String, val title: String) {
  Idled("idled", "Idling"),

  Completed("completed", "Completed");

  companion object {
    fun fromParam(param: String?): ItemTagState? = param?.let { paramMap[it] }

    private val paramMap = entries.associateBy(ItemTagState::param)
  }
}
