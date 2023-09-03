plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("org.sonarqube") version "4.2.1.3168"

}

sonarqube {
    properties {
        property("sonar.token", "sqp_62154e5de021eab8e5d9a4fbba81711c41845f1c")
        property("sonar.projectKey", "LeafStock")
        property("sonar.organization", "LeafStock")
        property("sonar.host.url", "http://localhost:9000")
    }
}
android {
    namespace = "com.prashant.stockmarketadviser"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.prashant.stockmarketadviser"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.1.1")
    implementation("com.google.firebase:firebase-database-ktx:20.2.2")
    implementation("com.google.firebase:firebase-inappmessaging-display:20.3.3")
    implementation("com.google.firebase:firebase-messaging:23.2.1")
    implementation("com.google.firebase:firebase-storage-ktx:20.2.1")
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.4.1")
    implementation("com.google.firebase:firebase-analytics-ktx:21.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Sdp
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.25")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.android.volley:volley:1.2.1")

    implementation("com.firebaseui:firebase-ui-database:7.1.1")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.github.GrenderG:Toasty:1.5.2")
    implementation("de.hdodenhof:circleimageview:3.1.0")

}