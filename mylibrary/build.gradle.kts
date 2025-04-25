import AppConfig.minSdk
import AppConfig.versionName
import Versions.compose_version
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    //id("com.apollographql.apollo3").version(Versions.wear_apollo_version)
    id("maven-publish")
}

android {
    namespace = "com.tvsm.connect"
    compileSdk = AppConfig.compileSdkV
    flavorDimensions.add("default")
    productFlavors {
        register("dev") {
            buildConfigField("String", "P360_URL", CommonUrl.P360_UAT_URL)
            buildConfigField("String", "P360_WSS_URL", CommonUrl.P360_WSS_UAT_URL)
        }
        register("staging") {
            buildConfigField("String", "P360_URL", CommonUrl.P360_UAT_URL)
            buildConfigField("String", "P360_WSS_URL", CommonUrl.P360_WSS_UAT_URL)
        }
        register("prod") {
            buildConfigField("String", "P360_URL", CommonUrl.P360_PRODUCTION_URL)
            buildConfigField("String", "P360_WSS_URL", CommonUrl.P360_WSS_PRODUCTION_URL)
        }
    }

    defaultConfig {
        namespace = "com.tvsm.connect"
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        multiDexEnabled = true
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            keyAlias = "tvs"
            keyPassword = "tvs@123"
            storeFile = file("keystore/tvs-keystore.jks")
            storePassword = "tvs@123"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            //isShrinkResources = true
           // proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }

        getByName("debug") {
            isMinifyEnabled = false
            //proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

//    lintOptions {
//        disable("InvalidPackage")
//    }

    hilt {
        enableAggregatingTask = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=compatibility")
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = compose_version
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
//    sourceSets {
//        getByName("main") {
//            java.srcDirs("src/main/java", "src/main/kotlin")
//        }
//    }
//    // Needed to publish AAR
    publishing {
        singleVariant("devDebug") {
            withSourcesJar()
        }
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(AppDependencies.wearPhoneInteractions)
    implementation(AppDependencies.wearRemoteInteractions)
    implementation(AppDependencies.wearTiles)
    coreLibraryDesugaring(AppDependencies.desugarJdkLibs)
    implementation(AppDependencies.ktxCore)
    implementation(AppDependencies.xRecyclerView)
    implementation(AppDependencies.xLegacySupport)
    implementation(AppDependencies.lcRuntimeKtx)
    implementation(AppDependencies.multiDex)
    implementation(AppDependencies.ktxCoroutineCore)
    implementation(AppDependencies.ktxCoroutineAndroid)
    implementation(AppDependencies.playServicesWearable)
    implementation(AppDependencies.accompanistPager)
    implementation(AppDependencies.gsonWearable)

    // Jetpack Compose
    implementation(AppDependencies.activityCompose)
    implementation(AppDependencies.composeMaterialIcon)
    implementation(AppDependencies.composeCompiler)
    implementation(AppDependencies.composeFoundation)
    implementation(AppDependencies.composeMaterial)
    implementation(AppDependencies.composeUi)
    implementation(AppDependencies.composeUiTooling)
    implementation(AppDependencies.composeUiToolingPreview)
    implementation(AppDependencies.composePercentlayout)

    // AndroidX Wear dependencies
    implementation(AppDependencies.wearComposeMaterial)
    implementation(AppDependencies.wearComposeFoundation)
    implementation(AppDependencies.wearComposeNavigation)
    implementation(AppDependencies.ktxCoroutinePlayService)

    // Testing Dependencies
    androidTestImplementation(AppDependencies.composeUiTestJunit)
    debugImplementation(AppDependencies.composeUiTestManifest)
    debugImplementation(AppDependencies.composeUiTooling)

    // Apollo GraphQL
    implementation(AppDependencies.wearApolloRuntime)
    implementation(AppDependencies.wearApolloAPi)
    // Glide Image lib
    implementation(AppDependencies.glideCompose)

    // Hilt-Dagger dependencies
    implementation(AppDependencies.hiltAndroid)
    kapt(AppDependencies.hiltAndroidCompiler)
    implementation(AppDependencies.hiltWork)
    kapt(AppDependencies.androidxHiltCompiler)
    // WorkManager
    implementation(AppDependencies.workRuntimeKtx)
    // OkHttp debug logging
    implementation(AppDependencies.okhttp3LoggingInterceptor)

    // Unit testing
    testImplementation(AppDependencies.wearJunit)
    testImplementation(AppDependencies.wearCoreTesting)
    testImplementation(AppDependencies.wearTurbine)
    testImplementation(AppDependencies.wearGoogleTruth)
    androidTestImplementation(AppDependencies.wearTestExt)
    testImplementation(AppDependencies.wearCoroutinesTest)
    testImplementation(AppDependencies.wearRoboElectric)
    testImplementation(AppDependencies.wearIoMock)

    // Mockito
    testImplementation(AppDependencies.wearMockitoCore)
    testImplementation(AppDependencies.wearMockitoInline)

    // Apollo
    implementation(AppDependencies.apolloRuntime)
    implementation(AppDependencies.apolloAPI)
    implementation(AppDependencies.apolloCache)
}

tasks.register<Jar>("sourceJar") {
    from(android.sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["devDebug"]) // <-- This includes the .aar with compiled code

                groupId = "com.tvsm"
                artifactId = "mylibrary"
                version = "1.2.4"

               // Source JAR
                // Sources JAR with classifier "sources"
//                artifact(tasks["sourceJar"]) {
//                    classifier = "sources"
//                    extension = "jar"
//                }

                // AAR file
                artifact("$buildDir/outputs/aar/mylibrary-dev-debug.aar"){
                    classifier="release"
                }

                pom {
                    name.set("MyLibrary")
                    description.set("A simple addition UI in Jetpack Compose")
                    url.set("https://github.com/harsh-tvs/myLib")

                    scm {
                        connection.set("scm:git:https://github.com/harsh-tvs/myLib.git")
                        developerConnection.set("scm:git:ssh://git@github.com:harsh-tvs/myLib.git")
                        url.set("https://github.com/harsh-tvs/myLib")
                    }
                }
            }
        }

        repositories {
            maven {
                val propsFile = rootProject.file("github.properties")
                val props = Properties().apply {
                    load(FileInputStream(propsFile))
                }

                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/harsh-tvs/myLib")
                credentials {
                    username = props["username"].toString()
                    password = props["token"].toString()
                }
            }
        }
    }
}
