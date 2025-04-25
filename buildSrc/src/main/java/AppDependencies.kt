import org.gradle.api.artifacts.dsl.DependencyHandler

object AppDependencies {
    // project class path
    const val gradle = "com.android.tools.build:gradle:${Versions.gradle}"
    const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val sonarQube = "org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:3.3"
    const val depVersionUpdates = "com.github.ben-manes:gradle-versions-plugin:0.39.0"
    const val advancedVersioning = "me.moallemi.gradle:advanced-build-version:1.7.3"
    const val hiltDep = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"
    const val navGraph = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navGraph}"
    const val firebaseGradle = "com.google.gms:google-services:${Versions.firebase}"
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-gradle:${Versions.firebaseCrashlytics}"

    // std kotlin compiler
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"

    // Kotlin Libs
    const val ktxCoroutineAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.ktxCoroutine}"
    const val ktxCoroutineCore =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.ktxCoroutine}"
    const val ktxCoroutinePlayService =
        "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.ktxCoroutine}"

    // androidx ui
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val androidMaterial =
        "com.google.android.material:material:${Versions.androidMaterial}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val ktxCore = "androidx.core:core-ktx:${Versions.ktxCore}"
    const val androidXBiometrics = "co.infinum:goldfinger:2.1.0"
    const val xBrowser = "androidx.browser:browser:1.3.0"
    const val xLocalBroadcastMngr =
        "androidx.localbroadcastmanager:localbroadcastmanager:1.0.0"
    const val xWork = "androidx.work:work-runtime:2.7.0"
    const val xLegacySupport = "androidx.legacy:legacy-support-v4:1.0.0"
    const val xLegacySupportV13 = "androidx.legacy:legacy-support-v13:1.0.0"
    const val xAnnotation = "androidx.annotation:annotation:1.2.0"
    const val xAnnotationExperimental = "androidx.annotation:annotation-experimental:1.1.0"
    const val xFragment = "androidx.fragment:fragment-ktx:1.5.0-alpha03"
    const val xActivity = "androidx.activity:activity-ktx:1.2.3"

    const val xCardView = "androidx.cardview:cardview:1.0.0"
    const val xRecyclerView = "androidx.recyclerview:recyclerview:1.2.1"
    const val xDataBinding = "androidx.databinding:databinding-runtime:7.4.1"
    const val featureDeliveryKtx = "com.google.android.play:feature-delivery-ktx:2.1.0"
    const val featureDelivery = "com.google.android.play:feature-delivery:2.1.0"
    const val xNavGraphFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navGraphUI}"
    const val xNavUiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navGraphUI}"
    const val xNavDynamic = "androidx.navigation:navigation-dynamic-features-fragment:${Versions.navGraphUI}"

    // ANDROID LIFECYCLE
    const val lcViewModel = "androidx.lifecycle:lifecycle-viewmodel:${Versions.androidxLifeCycle}"
    const val lcViewModelXtx =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.androidxLifeCycle}"

    const val lcLiveDataKtx =
        "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.androidxLifeCycle}" // api
    const val lcRuntimeKtx =
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidxLifeCycle}"
    const val lcViewModelSavedStateKtx =
        "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.androidxLifeCycle}"
    const val lcCommonKtx =
        "androidx.lifecycle:lifecycle-common-java8:${Versions.androidxLifeCycle}"
    const val lcServiceKtx =
        "androidx.lifecycle:lifecycle-service:${Versions.androidxLifeCycle}"

    // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
    const val lcProcessKtx =
        "androidx.lifecycle:lifecycle-process:${Versions.androidxLifeCycle}"

    // optional - ReactiveStreams support for LiveData
    const val lcReactiveStreamKtx =
        "androidx.lifecycle:lifecycle-reactivestreams-ktx:${Versions.androidxLifeCycle}"

    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxJavaAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxJavaAndroid}"
    const val rxJavaKtx = "io.reactivex.rxjava2:rxkotlin:${Versions.rxJavaKotlin}"

    // Bluetooth Library
    const val rxBleAndroid =
        "com.polidea.rxandroidble2:rxandroidble:${Versions.rxBleAndroid}"
    const val bleAndroid =
        "com.clj.fastble:FastBleLib:2.3.4"

    // Retrofit
    const val retrofit2 = "com.squareup.retrofit2:retrofit:${Versions.retrofit2}"
    const val retrofit2Rxjava =
        "com.squareup.retrofit2:adapter-rxjava3:${Versions.retrofit2}"
    const val retrofit2GsonConverter =
        "com.squareup.retrofit2:converter-gson:${Versions.retrofit2}"
    const val retrofit2MoshiConverter =
        "com.squareup.retrofit2:converter-moshi:${Versions.retrofit2}"
    const val retrofit2ConverterScaller =
        "com.squareup.retrofit2:converter-scalars:${Versions.retrofit2}"
    const val retrofit2Converter = "com.squareup.retrofit2:retrofit-converters:${Versions.retrofit2}"
    const val retrofit2Adapter = "com.squareup.retrofit2:retrofit-adapters:${Versions.retrofit2}"
    const val okHttpLogInterceptor =
        "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"
    const val okHttpTls = "com.squareup.okhttp3:okhttp-tls:${Versions.okHttp}"
    const val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"
    const val moshiKotlin = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
    const val moshiProcessor = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"

    // Dependency Injection
    const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.dagger}"
    const val daggerAndroidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    const val daggerAndroidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
    const val hiltNavigationFragment = "androidx.hilt:hilt-navigation-fragment:${Versions.hiltNavigationFrag}"
    const val hiltCompiler = "com.google.dagger:hilt-compiler:${Versions.hiltKapt}"

    // Room database
    const val roomDep = "androidx.room:room-runtime:${Versions.roomVersion}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.roomVersion}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.roomVersion}"
    const val roomTest = "androidx.room:room-testing:${Versions.roomVersion}" // androidTestImplementation

    const val pageindicator = "com.romandanylyk:pageindicatorview:1.0.3"

    // SQLite
    const val sqlCipher = "net.zetetic:android-database-sqlcipher:4.4.0"
    const val sqLite = "androidx.sqlite:sqlite:2.1.0"

    // third party
    const val lottie = "com.airbnb.android:lottie:3.4.2"
    const val shimmer = "com.facebook.shimmer:shimmer:0.5.0"
    const val datastore = "androidx.datastore:datastore-preferences:1.0.0"

    // LeakCanary
    const val leakcanary =
        "com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}"

    // Work manager

    // (Java only)
    const val workRuntime = "androidx.work:work-runtime:${Versions.work_version}"

    // Kotlin + coroutines
    const val workRuntimeKtx = "androidx.work:work-runtime-ktx:${Versions.work_version}"

    // optional - RxJava2 support
    const val workRxjava2 = "androidx.work:work-rxjava2:${Versions.work_version}"

    // optional - GCMNetworkManager support
    const val workGCM = "androidx.work:work-gcm:${Versions.work_version}"

    // optional - Test helpers
    const val workTesting = "androidx.work:work-testing:${Versions.work_version}"

    // optional - Multiprocess support
    const val workMultiprocess = "androidx.work:work-multiprocess:${Versions.work_version}"

    // Test
    const val junitTest = "junit:junit:${Versions.junit_version}" // testImplementation
    const val xJunitAndroidTest = "androidx.test.ext:junit:${Versions.androidx_junit_version}" // androidTestImplementation
    const val xEspressoAndroidTest = "androidx.test.espresso:espresso-core:3.5.1" // androidTestImplementation

    const val multiDex = "androidx.multidex:multidex:${Versions.multidex_version}"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide_version}"
    const val kotlinMetadataJvm = "org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.4.2"

    // timber
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val location = "com.google.android.gms:play-services-location:${Versions.location}"
    const val playServicesBasement = "com.google.android.gms:play-services-basement:${Versions.playServicesBasement}"

    val classpathLibs =
        arrayListOf<String>().apply {
            add(gradle)
            add(kotlinPlugin)
            add(hiltDep)
            add(navGraph)
            add(firebaseGradle)
            add(firebaseCrashlytics)
        }

    val androidLibs =
        arrayListOf<String>().apply {
            add(appcompat)
            add(androidMaterial)
            add(constraintLayout)
            add(ktxCore)
            add(xBrowser)
            add(xLocalBroadcastMngr)
            add(xWork)
            add(xLegacySupport)
            add(xAnnotation)
            add(xAnnotationExperimental)
            add(xActivity)
            add(xFragment)

            add(xCardView)
            add(xRecyclerView)
            add(featureDelivery)
            add(featureDeliveryKtx)
            add(xNavGraphFragment)
            add(xNavUiKtx)
            add(xNavDynamic)
        }
    val androidLifecycle =
        arrayListOf<String>().apply {
            add(lcViewModelXtx)
            add(lcLiveDataKtx)
            add(lcRuntimeKtx)
            add(lcViewModelSavedStateKtx)
            add(lcCommonKtx)
            add(lcServiceKtx)
            add(lcProcessKtx)
            add(lcReactiveStreamKtx)
        }

    val kotlinLibs =
        arrayListOf<String>().apply {
            add(kotlinStdLib)
            add(kotlinReflect)
            add(ktxCore)
            // add(ktxCoroutineBom)
            add(ktxCoroutineCore)
            add(ktxCoroutineAndroid)
        }

    val rxJavaLib =
        arrayListOf<String>().apply {
            add(rxJava)
            add(rxJavaAndroid)
            add(rxJavaKtx)
        }

    val networkLibs =
        arrayListOf<String>().apply {
            add(retrofit2)
            add(retrofit2Rxjava)
            add(retrofit2GsonConverter)
            add(retrofit2MoshiConverter)
            add(retrofit2ConverterScaller)
            add(retrofit2Converter)
            add(retrofit2Adapter)
            add(okHttpLogInterceptor)
            add(okHttpTls)
            add(moshi)
        }

    val moshiProcessors =
        arrayListOf<String>().apply {
            add(moshiProcessor)
        }

    val diLibs =
        arrayListOf<String>().apply {
            add(hiltAndroid)
        }

    val diProcessor =
        arrayListOf<String>().apply {
            add(hiltCompiler)
        }

    val bleLib =
        arrayListOf<String>().apply {
            add(rxBleAndroid)
        }

    val debugDependency =
        arrayListOf<String>().apply {
            add(leakcanary)
        }

    val testDependency =
        arrayListOf<String>().apply {
        }

    val roomProcessor =
        arrayListOf<String>().apply {
            add(roomCompiler)
        }

    val roomLib =
        arrayListOf<String>().apply {
            add(roomDep)
            add(roomKtx)
            add(sqLite)
            add(sqlCipher)
        }

    val sqLiteLib =
        arrayListOf<String>().apply {
            add(sqlCipher)
            add(sqLite)
        }
    val thirdParty =
        arrayListOf<String>().apply {
            add(shimmer)
            add(lottie)
        }

    val workManagerLibs =
        arrayListOf<String>().apply {
            add(workRuntime)
            add(workRuntimeKtx)
            add(workRxjava2)
            add(workGCM)
            add(workTesting)
            add(workMultiprocess)
        }

    // Apollo
    const val apolloRuntime = "com.apollographql.apollo3:apollo-runtime:${Versions.apollo_version}"
    const val apolloAPI = "com.apollographql.apollo3:apollo-api:${Versions.apollo_version}"
    const val apolloCache = "com.apollographql.apollo3:apollo-normalized-cache-sqlite:${Versions.apollo_version}"

    // firebase
    const val firebaseCrashlyticsKotlin = "com.google.firebase:firebase-crashlytics-ktx"
    const val firebaseAnalytics = "com.google.firebase:firebase-analytics-ktx"
    const val firebaseBOM = "com.google.firebase:firebase-bom:${Versions.firebase_bom_version}"
    const val firebaseMessaging = "com.google.firebase:firebase-messaging:${Versions.firebase_messaging}"

    // circular Progress Bar Lib
    const val circularProgressLib = "com.github.guilhe:circular-progress-view:${Versions.circular_progress_version}"

    // 3rd party lib for bottom Nav
    const val niceBottomLib = "com.github.ibrahimsn98:NiceBottomBar:${Versions.bottom_navbar_version}"

    // 3rd party lib for HTML data
    const val jsoupLib = "org.jsoup:jsoup:${Versions.jsoup_version}"
    const val cropimage = "com.theartofdev.edmodo:android-image-cropper:${Versions.cropimage_version}"

    // wear os versions
    const val playServicesWearable = "com.google.android.gms:play-services-wearable:${Versions.play_services_wearable_version}"
    const val wearApolloRuntime = "com.apollographql.apollo3:apollo-runtime:${Versions.wear_apollo_version}"
    const val wearApolloAPi = "com.apollographql.apollo3:apollo-api:${Versions.wear_apollo_version}"
    const val accompanistPager = "com.google.accompanist:accompanist-pager:${Versions.accompanist_version}"
    const val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:${Versions.desugar_version}"
    const val gsonWearable = "com.google.code.gson:gson:${Versions.gson_version}"
    const val glideCompose = "com.github.bumptech.glide:compose:1.0.0-alpha.3"
    const val hiltWork = "androidx.hilt:hilt-work:1.0.0"
    const val androidxHiltCompiler = "androidx.hilt:hilt-compiler:1.0.0"
    const val okhttp3LoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:4.9.0"

    // Jetpack Compose
    const val activityCompose = "androidx.activity:activity-compose:1.3.1"
    const val composeMaterialIcon = "androidx.compose.material:material-icons-extended:${Versions.compose_version}"
    const val composeCompiler = "androidx.compose.compiler:compiler:${Versions.compose_version}"
    const val composeFoundation = "androidx.compose.foundation:foundation:${Versions.compose_version}"
    const val composeMaterial = "androidx.compose.material:material:${Versions.compose_version}"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose_version}"
    const val composeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview:${Versions.compose_version}"
    const val composeUi = "androidx.compose.ui:ui:${Versions.compose_version}"
    const val composePercentlayout = "androidx.percentlayout:percentlayout:1.0.0"
    //const val composeNavigationAnimation = "com.google.accompanist:accompanist-navigation-animation:0.23.0"

    const val composeUiTestJunit = "androidx.compose.ui:ui-test-junit4:${Versions.compose_version}"
    const val composeUiTestManifest = "androidx.compose.ui:ui-test-manifest:${Versions.compose_version}"

    // androidx.wear dependencies
    const val wearComposeMaterial = "androidx.wear.compose:compose-material:${Versions.wear_compose_version}"
    const val wearComposeFoundation = "androidx.wear.compose:compose-foundation:${Versions.wear_compose_version}"
    const val wearComposeNavigation = "androidx.wear.compose:compose-navigation:${Versions.wear_compose_version}"
    const val wearPhoneInteractions = "androidx.wear:wear-phone-interactions:1.0.1"
    const val wearRemoteInteractions = "androidx.wear:wear-remote-interactions:1.0.0"
    const val wearTiles = "androidx.wear.tiles:tiles-material:1.1.0-alpha04"

    // Wear Unit Testing
    const val wearJunit = "junit:junit:4.13.2"
    const val wearCoreTesting = "androidx.arch.core:core-testing:2.1.0"
    const val wearTurbine = "app.cash.turbine:turbine:0.12.1"
    const val wearGoogleTruth = "com.google.truth:truth:1.1.3"
    const val wearTestExt = "androidx.test.ext:junit:1.1.5"
    const val wearCoroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4"
    const val wearMockitoCore = "org.mockito:mockito-core:3.10.0"
    const val wearMockitoInline = "org.mockito:mockito-inline:3.10.0"
    const val wearRoboElectric="org.robolectric:robolectric:4.8"
    const val wearIoMock="io.mockk:mockk:1.13.4"

    // dynamic dimentions
    const val sdp_android = "com.intuit.sdp:sdp-android:1.1.0"
}

fun DependencyHandler.firebase(){
    implementation(AppDependencies.firebaseBOM)
    implementation ("com.google.firebase:firebase-crashlytics:${Versions.firebaseCrash}")
    implementation ("com.google.firebase:firebase-analytics:${Versions.firebase_analytics}")
    implementation ("com.google.firebase:firebase-core:${Versions.firebase_core}")
}
