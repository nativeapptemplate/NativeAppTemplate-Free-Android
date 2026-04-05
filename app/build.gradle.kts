plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose)
  alias(libs.plugins.hilt)
  alias(libs.plugins.kotlin.parcelize)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
}

android {
  compileSdk = 36

  defaultConfig {
    applicationId = "com.nativeapptemplate.nativeapptemplatefree"
    targetSdk = 36
    minSdk = 26
    versionCode = 4
    versionName = "3.0.0"

    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    debug {
      extra["alwaysUpdateBuildId"] = false
      isDebuggable = true
      buildConfigField("String", "DOMAIN", "\"api.nativeapptemplate.com\"")
      buildConfigField("String", "PORT", "\"\"")
      buildConfigField("String", "SCHEME", "\"https\"")
//      buildConfigField("String", "DOMAIN","\"192.168.1.21\"")
//      buildConfigField("String", "PORT","\"3000\"")
//      buildConfigField("String", "SCHEME","\"http\"")
    }

    release {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      buildConfigField("String", "DOMAIN", "\"api.nativeapptemplate.com\"")
      buildConfigField("String", "PORT", "\"\"")
      buildConfigField("String", "SCHEME", "\"https\"")
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
  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.androidx.lifecycle.runtimeCompose)
  implementation(libs.androidx.lifecycle.viewModelCompose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.profileinstaller)
  implementation(libs.androidx.tracing.ktx)
  implementation(libs.capturable)
  implementation(libs.compose.qr.code)
  implementation(libs.hilt.android)
  implementation(libs.kotlin.stdlib.jdk8)
  implementation(libs.kotlinx.coroutines.guava)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.lottie.compose)
  implementation(libs.okhttp)
  implementation(libs.okhttp.logging.interceptor)
  implementation(libs.retrofit)
  implementation(libs.retrofit.kotlin.serialization)
  implementation(libs.sandwich)
  implementation(libs.sandwich.retrofit)
  implementation(libs.sandwich.retrofit.serialization)
  implementation(libs.tink.android)

  ksp(libs.hilt.compiler)

  debugImplementation(libs.androidx.compose.ui.tooling)

  testImplementation(libs.androidx.navigation.testing)
  testImplementation(libs.hilt.android.testing)
  testImplementation(libs.kotlin.test)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
}
