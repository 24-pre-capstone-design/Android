import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android") version "2.51"
}

android {
    namespace = "com.capston2024.capstonapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.capston2024.capstonapp"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "BASE_URL",
            gradleLocalProperties(rootDir, providers).getProperty("base.url")
        )

        buildConfigField(
            "String",
            "OPENAI_KEY",
            gradleLocalProperties(rootDir, providers).getProperty("openai.key")
        )
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
    packaging.resources {
        // Multiple dependency bring these files in. Exclude them to enable
        // our test APK to build (has no effect on our AARs)
        excludes += "/META-INF/AL2.0"
        excludes += "/META-INF/LGPL2.1"
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
        excludes += "META-INF/INDEX.LIST"
        excludes += "META-INF/DEPENDENCIES"
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
        buildConfig = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.drawerlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // define a BOM and its version
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.10.0"))

    // define any required OkHttp artifacts without version
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    //viewmodel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.activity:activity-ktx:1.8.2")

    // coil
    implementation("io.coil-kt:coil:2.4.0")

    //hilt
    implementation("com.google.dagger:hilt-android:2.46.1")
    kapt ("com.google.dagger:hilt-android-compiler:2.46.1")
    kapt ("com.google.dagger:dagger-android-processor:2.46.1")

    // timber
    implementation ("com.jakewharton.timber:timber:4.7.1")

    implementation("io.ktor:ktor-client-okhttp:2.3.0")
    implementation("com.aallam.openai:openai-client:3.7.2")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // To use generative AI
    implementation(fileTree("libs") { include("*.jar") })
    //implementation files("libs/gapic-google-cloud-ai-generativelanguage-v1beta2-java-0.0.0-SNAPSHOT.jar")

    implementation("com.google.api:gax:2.25.0")
    implementation("com.google.api:gax-grpc:2.25.0")
    implementation("com.google.api:gax-httpjson:0.110.0")
    implementation("io.grpc:grpc-okhttp:1.53.0")
    // tokenizer
    implementation("com.knuddels:jtokkit:0.6.1")
}

kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = false
}
