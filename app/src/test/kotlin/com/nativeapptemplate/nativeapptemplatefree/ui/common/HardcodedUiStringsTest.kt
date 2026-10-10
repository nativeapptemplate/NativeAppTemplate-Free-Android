package com.nativeapptemplate.nativeapptemplatefree.ui.common

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * User-facing text belongs in strings.xml, so it can be translated and reused. This scans the UI
 * sources for the usual ways a literal reaches the screen or TalkBack.
 */
class HardcodedUiStringsTest {
  private val uiSources = File("src/main/kotlin/com/nativeapptemplate/nativeapptemplatefree/ui")

  private val literalPatterns = listOf(
    Regex("""Text\(\s*"[A-Za-z]"""),
    Regex("""Icon\([^,]+,\s*"[A-Za-z]"""),
    Regex("""contentDescription\s*=\s*"[A-Za-z]"""),
    Regex("""append\(\s*"[A-Za-z ]{2,}"""),
    Regex("""actionLabel\s*(:\s*String\??\s*)?=\s*"[A-Za-z]"""),
    Regex("""(text|title|label|countLabel|titleText|bodyText|buttonTitle)\s*(:\s*String\s*)?=\s*"[A-Za-z]"""),
    Regex("""onShowSnackbar\([^,]+,\s*"[A-Za-z]"""),
  )

  // Shown only in debug builds, for developers.
  private val allowed = listOf("accountOwnerId:")

  @Test
  fun uiSources_haveNoHardcodedUserFacingStrings() {
    assertTrue("UI sources not found at ${uiSources.absolutePath}", uiSources.isDirectory)

    val offenders = uiSources.walkTopDown()
      .filter { it.isFile && it.extension == "kt" }
      .flatMap { file ->
        file.readLines().mapIndexedNotNull { index, line ->
          val hit = literalPatterns.any { it.containsMatchIn(line) } && allowed.none { it in line }
          if (hit) "${file.name}:${index + 1}: ${line.trim()}" else null
        }
      }
      .toList()

    assertTrue("Move these strings to strings.xml:\n" + offenders.joinToString("\n"), offenders.isEmpty())
  }
}
