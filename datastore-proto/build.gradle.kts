plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.protobuf)
}

kotlin {
  jvmToolchain(17)
}

protobuf {
  protoc {
    artifact = libs.protobuf.protoc.get().toString()
  }
  generateProtoTasks {
    all().configureEach {
      builtins {
        named("java") {
          option("lite")
        }
        register("kotlin") {
          option("lite")
        }
      }
    }
  }
}

dependencies {
  api(libs.protobuf.kotlin.lite)
}
