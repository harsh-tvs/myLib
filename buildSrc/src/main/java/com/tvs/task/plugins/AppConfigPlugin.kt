package com.tvs.task.plugins

import AppConfig
import AppDependencies
import AppDependencies.androidLibs
import AppDependencies.androidLifecycle
import LibModules
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AppConfigPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply("kotlin-android")
        project.plugins.apply("kotlin-parcelize")
        project.plugins.apply("kotlin-kapt")
        project.plugins.apply("dagger.hilt.android.plugin")
        project.plugins.apply("androidx.navigation.safeargs.kotlin")

        val androidExtension = project.extensions.getByName("android")
        if (androidExtension is BaseExtension) {
            androidExtension.apply {
                compileSdkVersion(AppConfig.compileSdkV)
                buildToolsVersion(AppConfig.buildToolsVersion)

                defaultConfig {
                    // applicationId = "com.tvs.TVSM" It is suppose to be set in actual app module. It can be
                    // different for multiple app module like IQube app module & TVS-Connect app module
                    minSdk = AppConfig.minSdk
                    targetSdk = AppConfig.targetSdk
                    versionCode = AppConfig.versionCode
                    versionName = AppConfig.versionName

                    testInstrumentationRunner = AppConfig.androidTestInstrumentation
                }

                // Configure common proguard file settings.
                val proguardFile = "proguard-rules.pro"

                when (this) {
                    is LibraryExtension -> defaultConfig {
                        consumerProguardFiles(AppConfig.proguardConsumerRules)
                    }
                    is AppExtension -> buildTypes {
                        getByName("release") {
                            isMinifyEnabled = false
                            isShrinkResources = false
                            proguardFiles(
                                getDefaultProguardFile("proguard-android-optimize.txt"),
                                proguardFile,
                            )
                        }

                        getByName("debug") {
                            isMinifyEnabled = false
                            isShrinkResources = false

                            (this as ExtensionAware).extra["alwaysUpdateBuildId"] = false
                            (this as ExtensionAware).extra["enableCrashlytics"] = false
                            proguardFiles(
                                getDefaultProguardFile("proguard-android-optimize.txt"),
                                proguardFile,
                            )
                        }
                    }
                }

                // Java 8
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }


            }
        }


    }
}
