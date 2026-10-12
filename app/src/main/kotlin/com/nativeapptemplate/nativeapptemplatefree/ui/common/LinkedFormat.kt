package com.nativeapptemplate.nativeapptemplatefree.ui.common

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle

/** A piece of a format string: either literal text or the 1-based index of a `%N$s` argument. */
sealed interface FormatPart {
  data class Literal(val text: String) : FormatPart
  data class Argument(val index: Int) : FormatPart
}

private val argumentPattern = Regex("%(\\d+)\\\$s")

/**
 * Splits a string resource such as "By signing up, you agree to the %1$s and %2$s." into its
 * literal text and its arguments, so each argument can be styled (for example as a link) while
 * the translation decides where the arguments go. Japanese puts them before the verb, so the
 * sentence cannot be glued together from English-ordered fragments.
 */
fun splitFormat(format: String): List<FormatPart> {
  val parts = mutableListOf<FormatPart>()
  var start = 0
  argumentPattern.findAll(format).forEach { match ->
    if (match.range.first > start) parts += FormatPart.Literal(format.substring(start, match.range.first))
    parts += FormatPart.Argument(match.groupValues[1].toInt())
    start = match.range.last + 1
  }
  if (start < format.length) parts += FormatPart.Literal(format.substring(start))
  return parts
}

/** A text link: what is shown and where it goes. */
data class FormatLink(val text: String, val url: String)

/**
 * Builds [format] with each `%N$s` replaced by the N-th of [links], drawn with [linkStyle]
 * among literal text drawn with [textStyle].
 */
fun linkedFormat(
  format: String,
  links: List<FormatLink>,
  textStyle: SpanStyle,
  linkStyle: SpanStyle,
): AnnotatedString = buildAnnotatedString {
  splitFormat(format).forEach { part ->
    when (part) {
      is FormatPart.Literal -> withStyle(textStyle) { append(part.text) }
      is FormatPart.Argument -> {
        val link = links[part.index - 1]
        withLink(LinkAnnotation.Url(link.url, TextLinkStyles(style = linkStyle))) {
          append(link.text)
        }
      }
    }
  }
}
