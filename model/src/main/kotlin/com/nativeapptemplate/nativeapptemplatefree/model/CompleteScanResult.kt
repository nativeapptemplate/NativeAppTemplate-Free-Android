package com.nativeapptemplate.nativeapptemplatefree.model

data class CompleteScanResult(
  var itemTagInfoFromNdefMessage: ItemTagInfoFromNdefMessage = ItemTagInfoFromNdefMessage(),
  var itemTagData: ItemTagData = ItemTagData(),
  var completeScanResultType: CompleteScanResultType = CompleteScanResultType.Idled,
  var message: String = "",
)
