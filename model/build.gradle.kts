plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.parcelize)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
}

android {
  compileSdk = 35

  defaultConfig {
    minSdk = 26

    buildTypes {
      release {
        isMinifyEnabled = false
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      }
    }
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
  implementation(libs.kotlin.stdlib.jdk8)
  implementation(libs.kotlinx.coroutines.guava)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.sandwich)
  implementation(libs.sandwich.retrofit)
}
