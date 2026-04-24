package com.nativeapptemplate.nativeapptemplatefree.model

data class ItemTagData(
  var id: String = "",
  var shopId: String = "",
  var name: String = "",
  var description: String = "",
  var position: Int? = null,
  var state: ItemTagState = ItemTagState.Idled,
  var createdAt: String = "",
  var completedAt: String = "",
  var shopName: String = "",
) {
  constructor(itemTag: ItemTag) : this(
    id = itemTag.getId(),
    shopId = itemTag.getShopId(),
    name = itemTag.getName(),
    description = itemTag.getDescription(),
    position = itemTag.getPosition(),
    state = ItemTagState.fromParam(itemTag.getState())!!,
    createdAt = itemTag.getCreatedAt()!!,
    completedAt = itemTag.getCompletedAt() ?: "",
    shopName = itemTag.getShopName(),
  )
}
