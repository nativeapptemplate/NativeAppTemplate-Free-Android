package com.nativeapptemplate.nativeapptemplatefree.i18n

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Every translatable string in values/strings.xml must have a Japanese version in
 * values-ja/strings.xml, with the same format arguments, so a Japanese user never sees an
 * English fallback or a crash from a missing `%1$s`.
 */
class StringResourcesTranslationTest {
  private val english = readStrings(File("src/main/res/values/strings.xml"))
  private val japanese = readStrings(File("src/main/res/values-ja/strings.xml"))

  private val translatableEnglish = english.filterValues { it.translatable }

  private val englishPlurals = readPlurals(File("src/main/res/values/strings.xml"))
  private val japanesePlurals = readPlurals(File("src/main/res/values-ja/strings.xml"))

  @Test
  fun everyTranslatableString_hasJapanese() {
    val missing = translatableEnglish.keys - japanese.keys
    assertTrue("Add these to values-ja/strings.xml: $missing", missing.isEmpty())
  }

  @Test
  fun japanese_hasNoStringsMissingFromEnglish() {
    val extra = japanese.keys - translatableEnglish.keys
    assertTrue("Not translatable or not in values/strings.xml: $extra", extra.isEmpty())
  }

  @Test
  fun formatArguments_matchBetweenEnglishAndJapanese() {
    val mismatches = translatableEnglish.mapNotNull { (name, en) ->
      val ja = japanese[name] ?: return@mapNotNull null
      val enArgs = formatArguments(en.text)
      val jaArgs = formatArguments(ja.text)
      if (enArgs == jaArgs) null else "$name: en=$enArgs ja=$jaArgs"
    }
    assertTrue("Format arguments differ:\n" + mismatches.joinToString("\n"), mismatches.isEmpty())
  }

  // The agent renames the domain words and the product name, so an article in front of one can't
  // follow the new word's first sound.
  @Test
  fun english_hasNoArticleBeforeRenamedWords() {
    val article = Regex("""\ban? (item tags?|shops?|shopkeepers?|NativeAppTemplate)\b""", RegexOption.IGNORE_CASE)
    val texts = english.mapValues { it.value.text } +
      englishPlurals.flatMap { (name, items) -> items.map { (quantity, text) -> "$name/$quantity" to text } }
    val wrong = texts.filterValues { article.containsMatchIn(it) }.keys
    assertTrue("Drop the article before the renamed word in: $wrong", wrong.isEmpty())
  }

  @Test
  fun plurals_matchBetweenEnglishAndJapanese() {
    assertEquals(englishPlurals.keys, japanesePlurals.keys)
    // Japanese has no plural forms, so only "other" is needed; it must take the English arguments.
    japanesePlurals.forEach { (name, items) ->
      assertEquals(setOf("other"), items.keys)
      val other = items.getValue("other")
      englishPlurals.getValue(name).forEach { (quantity, en) ->
        assertEquals("$name ($quantity)", formatArguments(en), formatArguments(other))
      }
    }
  }

  @Test
  fun japanese_usesTheSharedGlossary() {
    // The paid edition's wording for the app-language section of Edit Profile.
    assertEquals("アプリの言語", japanese.getValue("app_language").text)
    assertEquals("端末の設定で変更", japanese.getValue("change_app_language_in_settings").text)
    assertEquals("メールもこの言語で届きます。", japanese.getValue("app_language_emails_too").text)
    // The template API's config/locales/ja.yml: activerecord.models.shop = 店舗, item_tag = アイテムタグ.
    assertEquals("店舗", japanese.getValue("title_shops").text)
    assertEquals("店舗名", japanese.getValue("shop_name").text)
    assertEquals("アイテムタグを追加", japanese.getValue("label_add_item_tag").text)
    assertEquals("アイテムタグの管理", japanese.getValue("label_shop_settings_manage_item_tags").text)
    // Item tag states: idled = 未完了, completed = 完了.
    assertEquals("未完了", japanese.getValue("tag_idled").text)
    assertEquals("完了", japanese.getValue("tag_completed").text)
  }

  @Test
  fun japanese_shopIs店舗() {
    // The agent renames 店舗 as a plain substring, so shop must never be ショップ,
    // 店 or お店, or a generated app would keep this template's word.
    val katakana = japanese.filterValues { "ショップ" in it.text }.keys
    assertTrue("Use 店舗: $katakana", katakana.isEmpty())
    val otherShops = japanese.filterValues { "店" in it.text.replace("店舗", "") }.keys
    assertTrue("Use 店舗: $otherShops", otherShops.isEmpty())
  }

  @Test
  fun japanese_neverShortensアイテムタグ() {
    // The agent renames アイテムタグ as a plain substring, so item tag must always be
    // written in full; a bare タグ or アイテム would survive the rename.
    val shortened = japanese.filterValues { resource ->
      val rest = resource.text.replace("アイテムタグ", "")
      "タグ" in rest || "アイテム" in rest
    }.keys
    assertTrue("Write アイテムタグ in full: $shortened", shortened.isEmpty())
  }

  @Test
  fun japanese_neverMentionsOrganizations() {
    // The Free edition has no organizations (the paid edition's 組織), so a sentence reused from
    // the paid edition must not mention them, e.g. "件の店舗枠が残っています（すべての組織の合計）。".
    val organizations = japanese.filterValues { "組織" in it.text }.keys
    assertTrue("The Free edition has no organizations: $organizations", organizations.isEmpty())
  }

  @Test
  fun productName_isNotTranslated() {
    // The agent renames the English product name; a Japanese copy would escape the rename.
    assertTrue("app_name" !in japanese)
  }

  @Test
  fun languageNames_areNotTranslated() {
    // Each language is listed in its own name, whatever the app's language.
    assertTrue("language_english" !in japanese)
    assertTrue("language_japanese" !in japanese)
  }

  private data class StringResource(val text: String, val translatable: Boolean)

  /** `%1$s`, `%2$d`, ... sorted, so reordered arguments still match. Ignores the `%%` escape. */
  private fun formatArguments(text: String): List<String> =
    Regex("""%(\d+\$)?[-#+ 0,(]*\d*(\.\d+)?[a-zA-Z]""").findAll(text.replace("%%", ""))
      .map { it.value }
      .sorted()
      .toList()

  private fun readStrings(file: File): Map<String, StringResource> {
    assertTrue("Not found: ${file.absolutePath}", file.isFile)
    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
    val nodes = document.documentElement.getElementsByTagName("string")
    return (0 until nodes.length).associate { index ->
      val element = nodes.item(index) as Element
      element.getAttribute("name") to StringResource(
        text = unescape(element.textContent),
        translatable = element.getAttribute("translatable") != "false",
      )
    }
  }

  /** Plurals by name, each as quantity -> text. */
  private fun readPlurals(file: File): Map<String, Map<String, String>> {
    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
    val nodes = document.documentElement.getElementsByTagName("plurals")
    return (0 until nodes.length).associate { index ->
      val element = nodes.item(index) as Element
      val items = element.getElementsByTagName("item")
      element.getAttribute("name") to (0 until items.length).associate { itemIndex ->
        val item = items.item(itemIndex) as Element
        item.getAttribute("quantity") to unescape(item.textContent)
      }
    }
  }

  /** What Android shows for an XML string value: surrounding quotes dropped, `\n` etc. decoded. */
  private fun unescape(raw: String): String {
    val trimmed = raw.trim().let { if (it.length >= 2 && it.startsWith('"') && it.endsWith('"')) it.substring(1, it.length - 1) else it }
    return trimmed
      .replace("\\n", "\n")
      .replace("\\'", "'")
      .replace("\\\"", "\"")
  }
}
