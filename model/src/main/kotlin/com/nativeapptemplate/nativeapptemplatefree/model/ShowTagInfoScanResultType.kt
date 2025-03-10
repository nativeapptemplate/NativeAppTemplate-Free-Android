package com.nativeapptemplate.nativeapptemplatefree.model

enum class ShowTagInfoScanResultType(val param: String, val title: String) {
  Idled("idled","Idling"),

  Succeeded("succeeded","Succeeded"),

  Failed("failed","Failed");

  companion object {
    val titles: List<String> = entries.map { it.title }
    fun fromParam(param: String?): ShowTagInfoScanResultType? = param?.let { paramMap[it] }
    fun fromTitle(title: String): ShowTagInfoScanResultType? = titleMap[title]

    private val paramMap = entries.associateBy(ShowTagInfoScanResultType::param)
    private val titleMap = entries.associateBy(ShowTagInfoScanResultType::title)
  }
}
