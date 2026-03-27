// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
  repositories {
    google()
    mavenCentral()
  }

  dependencies {
    classpath(libs.google.oss.licenses.plugin) {
      exclude(group = "com.google.protobuf")
    }
  }
}

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.compose) apply false
  alias(libs.plugins.dependency.analysis) apply true
  alias(libs.plugins.gms) apply false
  alias(libs.plugins.hilt) apply false
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlin.parcelize) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.spotless) apply true
}

val ktlintEditorConfig =
  mapOf(
    "android" to "true",
    "ktlint_function_naming_ignore_when_annotated_with" to "Composable, Test",
    "ktlint_standard_backing-property-naming" to "disabled",
    "ktlint_standard_binary-expression-wrapping" to "disabled",
    "ktlint_standard_chain-method-continuation" to "disabled",
    "ktlint_standard_class-signature" to "disabled",
    "ktlint_standard_condition-wrapping" to "disabled",
    "ktlint_standard_function-expression-body" to "disabled",
    "ktlint_standard_function-literal" to "disabled",
    "ktlint_standard_function-type-modifier-spacing" to "disabled",
    "ktlint_standard_multiline-loop" to "disabled",
    "ktlint_standard_function-signature" to "disabled",
    "ktlint_standard_package-name" to "disabled",
    "ktlint_standard_no-wildcard-imports" to "disabled",
    "ktlint_standard_property-naming" to "disabled",
    "ktlint_standard_max-line-length" to "disabled",
    "ktlint_standard_filename" to "disabled",
  )

spotless {
  kotlin {
    target("**/*.kt")
    targetExclude("**/build/**")
    ktlint(libs.versions.ktlint.get())
      .editorConfigOverride(ktlintEditorConfig)
  }
  kotlinGradle {
    target("**/*.kts")
    targetExclude("**/build/**", "gradle/libs.versions.toml")
    ktlint(libs.versions.ktlint.get())
      .editorConfigOverride(ktlintEditorConfig)
  }
}
