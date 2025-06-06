import java.util.Properties
import java.io.FileInputStream

val localProps = Properties()
val localPropsFile = rootProject.file("local.properties")

if (localPropsFile.exists()) {
    localProps.load(FileInputStream(localPropsFile))
}

val bearerToken: String = localProps.getProperty("BEARER_TOKEN", "") ?: ""

plugins {
    alias(libs.plugins.android.application)
}


android {
    namespace = "com.inshorts.cinemax"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.inshorts.cinemax"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BEARER_TOKEN", "\"$bearerToken\"")

    }

    buildTypes {
        debug {
            buildConfigField("String", "BEARER_TOKEN", "\"$bearerToken\"")
        }
        release {
            buildConfigField("String", "BEARER_TOKEN", "\"$bearerToken\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
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
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.adapter.rxjava3)
    implementation(libs.converter.gson) // For JSON serialization

    //OkHttp
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // Room RxJava Support
    implementation(libs.room.rxjava3)

    // RxJava
    implementation(libs.rxjava)
    implementation(libs.rxandroid)

    // RxJava3 support for LiveData
    implementation(libs.reactivestreams)

}
