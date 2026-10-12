package com.nativeapptemplate.nativeapptemplatefree.demo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * The demo repositories' JSON lives in src/test/resources/demo. A .json file under an app source
 * set's assets (main, debug, release) would ship in the APK, mock accounts and all.
 */
class NoMockDataInReleaseTest {
  // Gradle runs unit tests from the module directory.
  private val sourceSets = File("src")

  @Test
  fun noAppSourceSetHasJsonAssets() {
    // Guards against passing vacuously from the wrong directory.
    assertTrue("run from ${File("").absolutePath}", File(sourceSets, "main/AndroidManifest.xml").isFile)

    val jsonAssets = sourceSets.listFiles().orEmpty()
      .filter { it.isDirectory && !it.name.startsWith("test") && !it.name.startsWith("androidTest") }
      .flatMap { sourceSet -> File(sourceSet, "assets").walk().filter { it.isFile && it.extension.equals("json", ignoreCase = true) }.toList() }
      .map { it.relativeTo(sourceSets).path }

    assertEquals(emptyList<String>(), jsonAssets)
  }
}
