plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) version "2.0.21"
    id("com.google.devtools.ksp")
    kotlin("kapt")
}

android {
    namespace = "com.task.pre_task_2502"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.task.pre_task_2502"
        minSdk = 26
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }

    // DataBinding 활성화
    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$room_version")
    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$room_version")


    val lifecycle_version = "2.8.7"
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")

    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")      // Retrofit 라이브러리
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0") // Gson 컨버터

    implementation ("com.github.bumptech.glide:glide:4.13.0")
    //annotationProcessor ("com.github.bumptech.glide:compiler:4.13.0")
    kapt("com.github.bumptech.glide:compiler:4.13.0")

    implementation("androidx.recyclerview:recyclerview:1.4.0")

    val paging_version = "3.3.2"

    implementation("androidx.paging:paging-runtime:$paging_version")
    implementation ("androidx.paging:paging-rxjava3:$paging_version") // RxJava 사용 시
    implementation ("androidx.paging:paging-guava:$paging_version") // Guava 사용 시

    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    implementation ("androidx.fragment:fragment-ktx:1.8.6")

    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    // To use constraintlayout in compose
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0")

    implementation ("com.google.android.material:material:1.10.0")

    kapt("groupId:artifactId:version")

    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    implementation("androidx.viewpager2:viewpager2:1.1.0")

}