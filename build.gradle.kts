// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
    }
    dependencies {
        val navVersion = "2.7.5"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
        classpath("com.google.firebase.firebase-perf:com.google.firebase.firebase-perf.gradle.plugin:1.4.2")
        classpath("com.google.firebase.crashlytics:com.google.firebase.crashlytics.gradle.plugin:2.9.9")
    }
}

plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false

    // Make sure that you have the Google services Gradle plugin dependency
    id("com.google.gms.google-services") version "4.4.0" apply false

    // Add the dependency for the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
}