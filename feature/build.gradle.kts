plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.aidevelopment.feature"
    compileSdk = 36 // ปรับตามโปรเจกต์จริง

    defaultConfig {
        minSdk = 31 // ปรับตามโปรเจกต์จริง
    }
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    // โยงไปยัง Core Module ของโปรเจกต์
    // implementation(project(":core:ui"))
    // implementation(project(":core:network"))
}
