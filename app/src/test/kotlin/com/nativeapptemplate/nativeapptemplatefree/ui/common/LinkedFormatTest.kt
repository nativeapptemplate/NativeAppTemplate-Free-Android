package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import org.junit.Assert.assertEquals
import org.junit.Test

class LinkedFormatTest {
  @Test
  fun splitFormat_keepsTheTranslationsWordOrder() {
    // values-ja "agreement": the links come before the verb.
    val parts = splitFormat("新規登録またはサインインすると、「%1\$s」と「%2\$s」に同意したものとみなされます。")

    assertEquals(
      listOf(
        FormatPart.Literal("新規登録またはサインインすると、「"),
        FormatPart.Argument(1),
        FormatPart.Literal("」と「"),
        FormatPart.Argument(2),
        FormatPart.Literal("」に同意したものとみなされます。"),
      ),
      parts,
    )
  }

  @Test
  fun splitFormat_handlesArgumentsAtTheEdgesAndOutOfOrder() {
    assertEquals(
      listOf(FormatPart.Argument(2), FormatPart.Literal("年"), FormatPart.Argument(1)),
      splitFormat("%2\$s年%1\$s"),
    )
  }

  @Test
  fun linkedFormat_putsEachLinkWhereTheTranslationSays() {
    val text = linkedFormat(
      format = "「%1\$s」と「%2\$s」に同意",
      links = listOf(FormatLink("利用規約", "https://example.com/terms"), FormatLink("プライバシーポリシー", "https://example.com/privacy")),
      textStyle = SpanStyle(),
      linkStyle = SpanStyle(),
    )

    // "「" is 1 char, "利用規約" 4, "」と「" 3, so the second link starts at 1 + 4 + 3 = 8.
    assertEquals("「利用規約」と「プライバシーポリシー」に同意", text.text)
    val links = text.getLinkAnnotations(0, text.length).map { (it.item as LinkAnnotation.Url).url to it.start }
    assertEquals(listOf("https://example.com/terms" to 1, "https://example.com/privacy" to 8), links)
  }
}
