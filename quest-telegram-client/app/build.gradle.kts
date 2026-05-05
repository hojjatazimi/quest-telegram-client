import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}

fun secretProperty(name: String): String {
    return localProperties.getProperty(name) ?: System.getenv(name).orEmpty()
}

fun buildConfigString(value: String): String {
    return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
}

android {
    namespace = "com.hojjatazimi.questtelegram"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hojjatazimi.questtelegram"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
        buildConfigField("int", "TELEGRAM_API_ID", secretProperty("TELEGRAM_API_ID").toIntOrNull()?.toString() ?: "0")
        buildConfigField("String", "TELEGRAM_API_HASH", buildConfigString(secretProperty("TELEGRAM_API_HASH")))
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    flavorDimensions += "telegramBackend"
    productFlavors {
        create("fake") {
            dimension = "telegramBackend"
            applicationIdSuffix = ".fake"
            versionNameSuffix = "-fake"
            buildConfigField("String", "TELEGRAM_BACKEND", "\"fake\"")
        }
        create("tdlib") {
            dimension = "telegramBackend"
            buildConfigField("String", "TELEGRAM_BACKEND", "\"tdlib\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.09.03")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.navigation:navigation-compose:2.8.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
