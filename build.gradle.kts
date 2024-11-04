//import org.jetbrains.kotlin.gradle.internal.kapt.incremental.UnknownSnapshot.classpath

buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        val nav_version = "2.8.3"
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}

//dependencies {
//    val navVersion = "2.8.3"
//    classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
//}