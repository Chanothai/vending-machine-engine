plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

android {
    namespace = "com.example.aidevelopment"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.aidevelopment"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

ktlint {
    android.set(true) // บังคับใช้ Rule ที่เหมาะกับ Android SDK
    outputToConsole.set(true) // ให้ปรินต์ Error ออกมาให้เห็นในหน้า Terminal ชัดๆ
    ignoreFailures.set(false) // ถ้าจัดหน้าผิด บังคับให้ Build พัง (ห้ามปล่อยผ่าน!)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

    // MockK
    testImplementation("io.mockk:mockk:1.13.8")

    // AssertJ
    testImplementation("org.assertj:assertj-core:3.24.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
