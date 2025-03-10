package com.nativeapptemplate.nativeapptemplatefree.model

enum class CompleteScanResultType(val param: String, val title: String) {
  Idled("idled","Idling"),

  Completed("completed","Completed!"),

  Reset("reset","Reset!"),

  Failed("failed","Failed");

  companion object {
    val titles: List<String> = entries.map { it.title }
    fun fromParam(param: String?): CompleteScanResultType? = param?.let { paramMap[it] }
    fun fromTitle(title: String): CompleteScanResultType? = titleMap[title]

    private val paramMap = entries.associateBy(CompleteScanResultType::param)
    private val titleMap = entries.associateBy(CompleteScanResultType::title)
  }
}
