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

    // Correct Kotlin DSL for packaging options
    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
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
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    implementation ("androidx.activity:activity:1.7.0")
    implementation ("androidx.appcompat:appcompat:1.7.0")

    implementation ("com.fasterxml.jackson.core:jackson-databind:2.14.0")
    implementation ("com.github.bumptech.glide:glide:4.13.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")



    implementation ("com.google.firebase:firebase-messaging:23.3.1")
    implementation ("com.google.auth:google-auth-library-oauth2-http:1.17.0")


}
