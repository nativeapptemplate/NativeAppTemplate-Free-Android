package com.nativeapptemplate.nativeapptemplatefree.model

enum class ItemTagType(val param: String, val title: String) {
  Server("server","Server"),

  Customer("customer","Customer");

  companion object {
    val titles: List<String> = entries.map { it.title }
    fun fromParam(param: String?): ItemTagType? = param?.let { paramMap[it] }
    fun fromTitle(title: String): ItemTagType? = titleMap[title]

    private val paramMap = entries.associateBy(ItemTagType::param)
    private val titleMap = entries.associateBy(ItemTagType::title)
  }
}
