import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ktfmt)
    alias(libs.plugins.sonar)
    // Necessary for Kotlin 2.0
    alias(libs.plugins.compose.compiler)

    id("jacoco")
    id("com.google.gms.google-services")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) // Ensure this is present
}

android {
    namespace = "com.android.brewr"
    compileSdk = 35

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(FileInputStream(localPropertiesFile))
    }
    // Load the API key from local.properties
    val mapsApiKey: String = localProperties.getProperty("MAPS_API_KEY") ?: ""
    val keyPW = System.getenv("KEY_PW") ?: localProperties.getProperty("KEY_PW")
    val keyAlias = System.getenv("KEY_ALIAS") ?: localProperties.getProperty("KEY_ALIAS")
    val ksFile = System.getenv("KS_FILE") ?: localProperties.getProperty("KS_FILE") // KS = keystore
    val ksPW = System.getenv("KS_PW") ?: localProperties.getProperty("KS_PW")

    defaultConfig {
        manifestPlaceholders += mapOf()
        applicationId = "com.android.brewr"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (ksFile != null && keyPW != null && keyAlias != null && ksPW != null) {
                println("we are here") // debug print
                signingConfig = signingConfigs.create("release") {
                    storeFile(file(ksFile)) // Path to your keystore
                    storePassword(ksPW) // Keystore
                    keyAlias(keyAlias) // Alias for the key to
                    keyPassword(keyPW) // Password for the key
                }
            }
        }

        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }

    testCoverage {
        jacocoVersion = "0.8.8"
    }

    buildFeatures {
        compose = true
        buildConfig=true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    // Robolectric needs to be run only in debug. But its tests are placed in the shared source set (test)
    // The next lines transfers the src/test/* from shared to the testDebug one
    //
    // This prevent errors from occurring during unit tests
    sourceSets.getByName("testDebug") {
        val test = sourceSets.getByName("test")

        java.setSrcDirs(test.java.srcDirs)
        res.setSrcDirs(test.res.srcDirs)
        resources.setSrcDirs(test.resources.srcDirs)
    }

    sourceSets.getByName("test") {
        java.setSrcDirs(emptyList<File>())
        res.setSrcDirs(emptyList<File>())
        resources.setSrcDirs(emptyList<File>())
    }
}

sonar {
    properties {
        property("sonar.projectKey", "BrewR-EPFL_BrewR")
        property("sonar.organization", "brewr")
        property("sonar.host.url", "https://sonarcloud.io")
        // Comma-separated paths to the various directories containing the *.xml JUnit report files. Each path may be absolute or relative to the project base directory.
        property("sonar.junit.reportPaths", "${project.layout.buildDirectory.get()}/test-results/testDebugunitTest/")
        // Paths to xml files with Android Lint issues. If the main flavor is changed, this file will have to be changed too.
        property("sonar.androidLint.reportPaths", "${project.layout.buildDirectory.get()}/reports/lint-results-debug.xml")
        // Paths to JaCoCo XML coverage report files.
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
    }
}

// When a library is used both by robolectric and connected tests, use this function
fun DependencyHandlerScope.globalTestImplementation(dep: Any) {
    androidTestImplementation(dep)
    testImplementation(dep)
}

dependencies {
    // ------------------- Firebase -------------------
    implementation(platform(libs.firebase.bom.v3200)) // Firebase BoM
    implementation(libs.google.firebase.auth.ktx) // Authentication
    implementation(libs.firebase.firestore.ktx) // Firestore
    implementation(libs.firebase.storage.ktx) // Storage

    // -------------- Google Play Services --------------
    implementation(libs.play.services.auth.v2050) // Auth (Google Sign-In)
    implementation(libs.play.services.location) // Location Services
    implementation(libs.play.services.maps) // Maps

    // ------------------- Credential Manager -------------------
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.google.identity.googleid)

    // ------------------- Jetpack -------------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.compose) // Navigation Component

    // -------------- Jetpack Compose ------------------
    implementation(platform(libs.compose.bom)) // Compose BoM
    implementation(libs.compose.ui) // Core UI
    implementation(libs.compose.ui.graphics) // Graphics
    implementation(libs.compose.material3) // Material Design 3
    implementation(libs.compose.activity) // Activity integration
    implementation(libs.compose.viewmodel) // ViewModel integration
    implementation(libs.compose.preview) // Preview support
    debugImplementation(libs.compose.tooling) // Debug tooling

    // -------------------- Maps --------------------
    implementation(libs.maps.compose) // Compose Maps
    implementation(libs.places) // Places API

    // ------------------- Image Loading -------------------
    implementation(libs.coil.compose) // Coil for Compose

    // -------------------- Testing --------------------
    testImplementation(libs.junit) // JUnit
    testImplementation(libs.mockk) // MockK
    testImplementation(libs.mockito.core) // Mockito Core
    testImplementation(libs.mockito.kotlin) // Mockito Kotlin
    testImplementation(libs.robolectric) // Robolectric
    androidTestImplementation(libs.mockito.android) // Mockito Android

    // ------------- Compose Testing -----------------
    globalTestImplementation(platform(libs.compose.bom))
    globalTestImplementation(libs.compose.test.junit)
    debugImplementation(libs.compose.test.manifest)

    // --------------- AndroidX Testing ----------------
    globalTestImplementation(libs.androidx.junit)
    globalTestImplementation(libs.androidx.espresso.core)

    // ----------- Kaspresso Test Framework -----------
    globalTestImplementation(libs.kaspresso)
    globalTestImplementation(libs.kaspresso.compose)
}

tasks.withType<Test> {
    // Configure Jacoco for each tests
    description = "Configure Jacoco to include no-location classes and exclude JDK internal classes"
    group = "Verification"
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register("jacocoTestReport", JacocoReport::class) {
    mustRunAfter("testDebugUnitTest", "connectedDebugAndroidTest")

    reports {
        xml.required = true
        html.required = true
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
    )

    val debugTree = fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    val mainSrc = "${project.layout.projectDirectory}/src/main/java"
    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(project.layout.buildDirectory.get()) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        include("outputs/code_coverage/debugAndroidTest/connected/*/coverage.ec")
    })
}