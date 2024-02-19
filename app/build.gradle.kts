plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
    id("AndResGuard")
    kotlin("kapt")
}


android {
    namespace = "com.msandypr.thesandynews"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.msandypr.thesandynews"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.1"

        buildConfigField("Boolean", "ENABLE_CRASHLYTICS", "true")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Komponen Arsitektur
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // Room Database
    implementation ("androidx.room:room-runtime:2.6.1")
    ksp ("androidx.room:room-compiler:2.6.1")

    // Kotlin Extensions + Coroutines support buat Room
    implementation ("androidx.room:room-ktx:2.6.1")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // Coroutine Lifecycle Scopes
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.5.0")

    // Navigation Components
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.6")

    // Glide
    annotationProcessor ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    ksp ("com.github.bumptech.glide:compiler:4.12.0")

    //ML Kit Barcode
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))

    // Add the dependencies for the Crashlytics and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")

    //rootbeer
    implementation ("com.scottyab:rootbeer-lib:0.0.9")

}

andResGuard {
    // mappingFile = file("./resource_mapping.txt")
    mappingFile = null
    use7zip = true
    useSign = true
    // It will keep the origin path of your resources when it's true
    keepRoot = false
    // If set, name column in arsc those need to proguard will be kept to this value
    fixedResName = "arg"
    // It will merge the duplicated resources, but don't rely on this feature too much.
    // it's always better to remove duplicated resource from repo
    mergeDuplicatedRes = true
    whiteList = listOf(
        // your icon
        "R.drawable.icon",
        // for fabric
        "R.string.com.crashlytics.*",
        // for google-services
        "R.string.google_app_id",
        "R.string.gcm_defaultSenderId",
        "R.string.default_web_client_id",
        "R.string.ga_trackingId",
        "R.string.firebase_database_url",
        "R.string.google_api_key",
        "R.string.google_crash_reporting_api_key",
        "R.string.project_id"
    )
    compressFilePattern = listOf(
        "*.png",
        "*.jpg",
        "*.jpeg",
        "*.gif"
    )
    sevenzip {
        path = "C:/apktool/7z2301-x64.exe"
    }

    /**
     * Optional: if finalApkBackupPath is null, AndResGuard will overwrite final apk
     * to the path which assemble[Task] write to
     **/
    // finalApkBackupPath = "${project.rootDir}/final.apk"

    /**
     * Optional: Specifies the name of the message digest algorithm to user when digesting the entries of JAR file
     * Only works in V1signing, default value is "SHA-1"
     **/
    // digestalg = "SHA-256"
}

