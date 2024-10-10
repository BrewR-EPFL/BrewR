// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.compose.compiler) apply false // Compose compiler pugin necessary for Kotlin 2.0

}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
    }
}
