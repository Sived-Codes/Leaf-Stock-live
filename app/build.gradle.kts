plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

}

android {
    namespace = "com.prashant.stockmarketadviser"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.prashant.stockmarketadviser"
        minSdk = 21
        targetSdk = 34
        versionCode = 3
        versionName = "1.2"

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

    implementation("com.google.android.gms:play-services-ads:22.4.0")
    implementation ("com.google.android.play:core:1.10.3")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")



    //Firebase
    implementation("com.firebaseui:firebase-ui-database:7.1.1")
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    implementation("com.google.firebase:firebase-appcheck-ktx")
    implementation("com.google.firebase:firebase-auth:22.1.2")
    implementation("com.google.firebase:firebase-database-ktx:20.2.2")
    implementation("com.google.firebase:firebase-inappmessaging-display:20.3.5")
    implementation("com.google.firebase:firebase-messaging:23.2.1")
    implementation("com.google.firebase:firebase-storage-ktx:20.2.1")
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.4.3")
    implementation("com.google.firebase:firebase-analytics-ktx:21.3.0")
    implementation("com.google.firebase:firebase-auth-ktx:22.1.2")
    implementation("com.google.firebase:firebase-inappmessaging-display-ktx:20.3.5")
    implementation("com.google.firebase:firebase-messaging-ktx:23.2.1")

    //Sdp responsive size
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    //Gif viewer
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.25")
    //Circular dp
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //Picasso Image
    implementation("com.squareup.picasso:picasso:2.71828")

    //Custom Toast
    implementation("com.github.GrenderG:Toasty:1.5.2")

    //Otp pin view
    implementation("io.github.chaosleung:pinview:1.4.4")

    //Volley
    implementation("com.android.volley:volley:1.2.1")

}