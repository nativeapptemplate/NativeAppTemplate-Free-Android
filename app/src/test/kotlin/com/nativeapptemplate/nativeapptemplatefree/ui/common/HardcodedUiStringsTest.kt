package com.nativeapptemplate.nativeapptemplatefree.ui.common

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * User-facing text belongs in strings.xml, so it can be translated (values-ja) and reused. This
 * scans the UI sources for the usual ways a literal reaches the screen or TalkBack. Each file is
 * scanned as a whole, so a literal on the line after `Text(` is caught too.
 */
class HardcodedUiStringsTest {
  private val uiSources = File("src/main/kotlin/com/nativeapptemplate/nativeapptemplatefree/ui")

  private val literalPatterns = listOf(
    Regex("""Text\(\s*"[A-Za-z]"""),
    // A sentence built around a template, e.g. "$name invited you" or "${count} characters".
    Regex("""(Text\(|text\s*=)\s*"\$(\{[^}]+\}|[A-Za-z]+)\s+[A-Za-z]"""),
    Regex("""Icon\([^,)]+,\s*"[A-Za-z]"""),
    Regex("""InfoRow\([^,)]+,\s*"[A-Za-z]"""),
    Regex("""(updateMessage\(|message\s*=)\s*"[A-Za-z]"""),
    Regex("""\?:\s*"[A-Za-z]{2,}"""),
    Regex("""contentDescription\s*=\s*"[A-Za-z]"""),
    Regex("""append\(\s*"[A-Za-z ]{2,}"""),
    Regex("""actionLabel\s*(:\s*String\??\s*)?=\s*"[A-Za-z]"""),
    Regex("""(text|title|label|countLabel|titleText|bodyText|buttonTitle)\s*(:\s*String\s*)?=\s*"[A-Za-z]"""),
    Regex("""onShowSnackbar\([^,)]+,\s*"[A-Za-z]"""),
  )

  // Shown only in debug builds, for developers.
  private val allowed = listOf("accountOwnerId:")

  @Test
  fun uiSources_haveNoHardcodedUserFacingStrings() {
    assertTrue("UI sources not found at ${uiSources.absolutePath}", uiSources.isDirectory)

    val offenders = uiSources.walkTopDown()
      .filter { it.isFile && it.extension == "kt" }
      .flatMap { file ->
        val source = file.readText()
        literalPatterns.flatMap { pattern -> pattern.findAll(source).toList() }
          .filter { match -> allowed.none { it in lineAt(source, match.range.first) } }
          .map { match -> "${file.name}:${lineNumberAt(source, match.range.first)}: ${match.value.replace(Regex("""\s+"""), " ")}" }
      }
      .distinct()
      .toList()

    assertTrue("Move these strings to strings.xml:\n" + offenders.joinToString("\n"), offenders.isEmpty())
  }

  private fun lineNumberAt(source: String, index: Int): Int = source.substring(0, index).count { it == '\n' } + 1

  private fun lineAt(source: String, index: Int): String {
    val start = source.lastIndexOf('\n', index) + 1
    val end = source.indexOf('\n', index).let { if (it == -1) source.length else it }
    return source.substring(start, end)
  }
}
