package com.nativeapptemplate.nativeapptemplatefree.model

data class ItemTagInfoFromNdefMessage(
  var id: String = "",
  var itemTagType: ItemTagType = ItemTagType.Server,
  var success: Boolean = false,
  var message: String = "",

  var isReadOnly : Boolean = false,
  var scannedAt: String = "",
)
