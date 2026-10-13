plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.parcelize)
  alias(libs.plugins.kotlin.serialization)
}

android {
  compileSdk = 37

  defaultConfig {
    minSdk = 26
  }

  kotlin {
    jvmToolchain(17)
  }

  java {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(17))
    }
  }

  namespace = "com.nativeapptemplate.nativeapptemplatefree.model"
}

dependencies {
  implementation(libs.kotlinx.serialization.json)

  testImplementation(libs.junit)
}
