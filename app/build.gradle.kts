import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.tracksolutions"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.tracksolutions"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        externalNativeBuild {
            // For ndk-build, instead use the ndkBuild block.
            cmake {
                // Passes optional arguments to CMake.
                arguments += listOf("-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON")
            }
        }

        // (Opcional) Reducir tamaño de APK si querés limitar ABIs
        // ndk {
        //     abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        // }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
        buildConfig = true
    }

    fun Packaging.() {
        jniLibs {
            useLegacyPackaging = true
        }
    }

    // (Opcional) Si llegás a tener warnings por licencias duplicadas:
    // packaging {
    //     resources {
    //         excludes += setOf(
    //             "META-INF/DEPENDENCIES",
    //             "META-INF/NOTICE",
    //             "META-INF/NOTICE.txt",
    //             "META-INF/LICENSE",
    //             "META-INF/LICENSE.txt"
    //         )
    //     }
    // }
}

dependencies {
    // Core/Activity/Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM + UI + Material3
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Room + KSP
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Si usás SQLCipher con Room, NO agregues room-sqlite directamente: Room toma el helper de la factory

    // SQLite helpers (necesarios para SupportSQLite + Room)
    implementation("androidx.sqlite:sqlite:2.4.0")
    implementation("androidx.sqlite:sqlite-framework:2.4.0")
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")
    // SQLCipher (cifrado)
////    implementation("net.zetetic:android-database-sqlcipher:4.5.6@aar")
//    implementation("net.zetetic:sqlcipher-android:4.5.6")

    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // Firebase (usa un solo BOM)
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation(libs.firebase.database)

    // Tests/Debug
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // (Si lo usabas) ANTLR externo de Room para schema export; si no te hace falta, podés quitarlo
    implementation(libs.androidx.room.external.antlr)
}
