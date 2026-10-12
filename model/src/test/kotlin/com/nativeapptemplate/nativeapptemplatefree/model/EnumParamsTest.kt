package com.nativeapptemplate.nativeapptemplatefree.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** The params are the API's values; the expected ones are those the iOS template reads (ItemTagState.init(string:)). */
class EnumParamsTest {
  @Test
  fun itemTagState_params() {
    assertEquals(ItemTagState.Idled, ItemTagState.fromParam("idled"))
    assertEquals(ItemTagState.Completed, ItemTagState.fromParam("completed"))
    assertNull(ItemTagState.fromParam("unknown"))
    assertNull(ItemTagState.fromParam(null))
  }
}
