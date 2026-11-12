import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

// Load signing properties & optional telemetry keys
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystorePropertiesFile.inputStream().use { stream ->
        keystoreProperties.load(stream)
    }
}

val aptabaseKey: String = System.getenv("APTABASE_KEY")
    ?: keystoreProperties.getProperty("aptabaseKey", "")
val aptabaseHost: String = System.getenv("APTABASE_HOST")
    ?: keystoreProperties.getProperty("aptabaseHost", "")

// Kept at top level for .github/workflows/android-release.yml (workflow_dispatch grep)
val appVersion = "1.4.4"

val appVersionCode = 315041

android {
    namespace = "de.goork.mapflip"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.goork.mapflip"
        minSdk = 26
        targetSdk = 36
        // IMPORTANT: Must remain static literals (not variable references) for F-Droid's checkupdates.py regex parser
        versionCode = 315041
        versionName = "1.4.4"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    if (defaultConfig.versionCode != appVersionCode) {
        throw GradleException("versionCode mismatch in app/build.gradle.kts: defaultConfig.versionCode (${defaultConfig.versionCode}) does not match appVersionCode ($appVersionCode)")
    }
    if (defaultConfig.versionName != appVersion) {
        throw GradleException("versionName mismatch in app/build.gradle.kts: defaultConfig.versionName (${defaultConfig.versionName}) does not match appVersion ($appVersion)")
    }

    signingConfigs {
        create("release") {
            if (keystoreProperties.isNotEmpty()) {
                val sFile = keystoreProperties.getProperty("storeFile")
                val sPass = keystoreProperties.getProperty("storePassword")
                val kAlias = keystoreProperties.getProperty("keyAlias")
                val kPass = keystoreProperties.getProperty("keyPassword")
                if (sFile != null && sPass != null && kAlias != null && kPass != null) {
                    storeFile = rootProject.file(sFile)
                    storePassword = sPass
                    keyAlias = kAlias
                    keyPassword = kPass
                }
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (keystoreProperties.isNotEmpty()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    flavorDimensions.add("distribution")
    productFlavors {
        create("play") {
            dimension = "distribution"
            buildConfigField("String", "APTABASE_KEY", "\"$aptabaseKey\"")
            buildConfigField("String", "APTABASE_HOST", "\"$aptabaseHost\"")
        }
        create("foss") {
            dimension = "distribution"
            buildConfigField("String", "APTABASE_KEY", "\"\"")
            buildConfigField("String", "APTABASE_HOST", "\"\"")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.compose.ui.test.junit4)

    // Google Play In-App Review API (Play Store Flavor only)
    add("playImplementation", libs.play.review)

    androidTestImplementation(libs.screengrab)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
}
androidComponents {
    beforeVariants(selector().withBuildType("release")) { variantBuilder ->
        @Suppress("DEPRECATION")
        variantBuilder.enableUnitTest = false
    }
}
