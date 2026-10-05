plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose)
  alias(libs.plugins.hilt)
  alias(libs.plugins.kotlin.parcelize)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
}

android {
  compileSdk = 37

  defaultConfig {
    applicationId = "com.nativeapptemplate.nativeapptemplatefree"
    targetSdk = 36
    minSdk = 26
    versionCode = 11
    versionName = "3.2.6"

    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    debug {
      extra["alwaysUpdateBuildId"] = false
      isDebuggable = true
      apiBuildConfigFields()
    }

    release {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      apiBuildConfigFields()
    }
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }

  kotlin {
    jvmToolchain(17)
    compilerOptions {
      freeCompilerArgs.addAll(
        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
      )
    }
  }

  java {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(17))
    }
  }

  packaging {
    resources {
      excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
  }
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }

  namespace = "com.nativeapptemplate.nativeapptemplatefree"
}

dependencies {
  implementation(project(":model"))
  implementation(project(":datastore-proto"))

  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.foundation.layout)
  implementation(libs.androidx.compose.material.iconsExtended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.datastore.core)
  implementation(libs.androidx.hilt.lifecycle.viewModelCompose)
  implementation(libs.androidx.lifecycle.runtimeCompose)
  implementation(libs.androidx.lifecycle.viewModelCompose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.profileinstaller)
  implementation(libs.androidx.tracing.ktx)
  implementation(libs.hilt.android)
  implementation(libs.kotlin.stdlib.jdk8)
  implementation(libs.kotlinx.coroutines.guava)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.okhttp)
  implementation(libs.okhttp.logging.interceptor)
  implementation(libs.retrofit)
  implementation(libs.retrofit.kotlin.serialization)
  implementation(libs.sandwich)
  implementation(libs.sandwich.retrofit)
  implementation(libs.tink.android)

  ksp(libs.hilt.compiler)

  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  testImplementation(platform(libs.androidx.compose.bom))
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.navigation.testing)
  testImplementation(libs.hilt.android.testing)
  testImplementation(libs.kotlin.test)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
}

/**
 * The API endpoint, from ~/.gradle/gradle.properties (or -P), for every build type.
 * NATIVEAPPTEMPLATE_API_CERT_PINS: optional comma-separated "sha256/..." pins for the API domain.
 */
fun com.android.build.api.dsl.ApplicationBuildType.apiBuildConfigFields() {
  fun property(name: String): String? = (project.findProperty(name) as String?)?.trim()?.takeIf { it.isNotEmpty() }

  buildConfigField("String", "DOMAIN", "\"${property("NATIVEAPPTEMPLATE_API_DOMAIN") ?: "api.nativeapptemplate.com"}\"")
  buildConfigField("String", "PORT", "\"${property("NATIVEAPPTEMPLATE_API_PORT") ?: ""}\"")
  buildConfigField("String", "SCHEME", "\"${property("NATIVEAPPTEMPLATE_API_SCHEME") ?: "https"}\"")
  buildConfigField("String", "CERT_PINS", "\"${property("NATIVEAPPTEMPLATE_API_CERT_PINS") ?: ""}\"")
}
