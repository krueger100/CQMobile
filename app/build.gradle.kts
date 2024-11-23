plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.cq_mobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cq_mobile"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.database)
    implementation(libs.play.services.location)
    implementation(libs.ui.text.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.google.android.material:material:1.6.0")
    implementation("com.akexorcist:google-direction-library:1.2.1")
    implementation("com.google.firebase:firebase-database:20.0.5")
    implementation("com.google.firebase:firebase-auth:21.0.5")

    implementation ("androidx.recyclerview:recyclerview:1.3.1")
    implementation ("com.firebaseui:firebase-ui-database:8.0.1")

    implementation ("com.firebaseui:firebase-ui-database:8.0.1")
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.1")

    implementation("com.firebaseui:firebase-ui-database:8.0.1") // Firebase UI Database
    implementation("androidx.paging:paging-runtime:3.1.1") // Paging Library
    implementation("com.google.firebase:firebase-database:20.0.5") // Firebase Realtime Database

    implementation ("com.squareup.retrofit2:retrofit:2.11.0");
    implementation("com.android.volley:volley:1.2.1")
    implementation ("com.google.code.gson:gson:2.11.0")



}
