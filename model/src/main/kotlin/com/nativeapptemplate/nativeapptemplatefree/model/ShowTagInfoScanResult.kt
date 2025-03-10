package com.nativeapptemplate.nativeapptemplatefree.model

data class ShowTagInfoScanResult(
  var itemTagInfoFromNdefMessage: ItemTagInfoFromNdefMessage = ItemTagInfoFromNdefMessage(),
  var itemTagData: ItemTagData = ItemTagData(),
  var showTagInfoScanResultType: ShowTagInfoScanResultType = ShowTagInfoScanResultType.Idled,
  var message: String = "",
)
