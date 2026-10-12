package com.nativeapptemplate.nativeapptemplatefree.i18n

import com.nativeapptemplate.nativeapptemplatefree.model.Locales
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Android 13+ lists the app under Settings > Apps > Language only when the manifest points
 * android:localeConfig at res/xml/locales_config.xml, and offers exactly the languages listed
 * there. They must be the ones the app is translated into and the API supports.
 */
class LocalesConfigTest {
  private val manifest = File("src/main/AndroidManifest.xml")
  private val localesConfig = File("src/main/res/xml/locales_config.xml")

  @Test
  fun manifest_declaresTheLocalesConfig() {
    val application = parse(manifest).getElementsByTagName("application").item(0) as Element
    assertEquals("@xml/locales_config", application.getAttribute("android:localeConfig"))
  }

  @Test
  fun localesConfig_listsTheSupportedLanguages() {
    val nodes = parse(localesConfig).getElementsByTagName("locale")
    val languages = (0 until nodes.length).map { (nodes.item(it) as Element).getAttribute("android:name") }

    // values/ is English and values-ja/ Japanese; Locales.supported is what the API accepts.
    assertEquals(listOf("en", "ja"), languages)
    assertEquals(Locales.supported, languages)
  }

  @Test
  fun everyListedLanguage_hasItsStrings() {
    // English is the unqualified values/; any other listed language needs values-<lang>/.
    parse(localesConfig).getElementsByTagName("locale").let { nodes ->
      (0 until nodes.length).map { (nodes.item(it) as Element).getAttribute("android:name") }
    }.filter { it != Locales.DEFAULT }.forEach { language ->
      assertTrue("Missing values-$language/strings.xml", File("src/main/res/values-$language/strings.xml").isFile)
    }
  }

  private fun parse(file: File) = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).also {
    assertTrue("Not found: ${file.absolutePath}", file.isFile)
  }
}
