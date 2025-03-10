package com.nativeapptemplate.nativeapptemplatefree.model

data class ItemTagData(
  var id: String = "",
  var shopId: String = "",
  var queueNumber: String = "",
  var state: ItemTagState = ItemTagState.Idled,
  var scanState: ScanState = ScanState.Unscanned,
  var createdAt: String =  "",
  var customerReadAt: String=  "",
  var completedAt: String=  "",
  var shopName: String = "",
  var alreadyCompleted: Boolean = false,
) {
  constructor(itemTag: ItemTag) : this(
    id = itemTag.getId(),
    shopId = itemTag.getShopId(),
    queueNumber = itemTag.getQueueNumber(),
    state = ItemTagState.fromParam(itemTag.getState())!!,
    scanState = itemTag.getScanState(),
    createdAt = itemTag.getCreatedAt()!!,
    customerReadAt = itemTag.getCustomerReadAt() ?: "",
    completedAt = itemTag.getCompletedAt() ?: "",
    shopName = itemTag.getShopName(),
    alreadyCompleted = itemTag.getAlreadyCompleted(),
  )
}
