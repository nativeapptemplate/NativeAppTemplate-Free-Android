package com.nativeapptemplate.nativeapptemplatefree.ui.common

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * A validation message must be left out of the composition while the input is valid. Drawing it
 * in Color.Transparent hides it from the eye only: it stays in the accessibility tree, so TalkBack
 * (and UI Automator) still reports "Name is invalid." after the user has fixed the name.
 * ItemTagEditViewTest checks one form end to end; this catches the
 * pattern on every other form.
 */
class HiddenValidationMessagesTest {
  private val uiSources = File("src/main/kotlin/com/nativeapptemplate/nativeapptemplatefree/ui")

  @Test
  fun uiSources_doNotHideValidationMessagesWithATransparentColor() {
    assertTrue("UI sources not found at ${uiSources.absolutePath}", uiSources.isDirectory)

    val pattern = Regex("""else\s+Color\.Transparent""")
    val offenders = uiSources.walkTopDown()
      .filter { it.isFile && it.extension == "kt" }
      .flatMap { file ->
        file.readLines().mapIndexedNotNull { index, line ->
          if (pattern.containsMatchIn(line)) "${file.name}:${index + 1}: ${line.trim()}" else null
        }
      }
      .toList()

    assertTrue(
      "Show these messages with `if (invalid) Text(...)` instead:\n" + offenders.joinToString("\n"),
      offenders.isEmpty(),
    )
  }
}
