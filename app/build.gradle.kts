import java.util.Properties
import java.io.File
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.cesavesin.registrodemovilizacion"
    compileSdk = 35  // Cambiado de 35 a 33

    defaultConfig {
        applicationId = "com.cesavesin.registrodemovilizacion"
        minSdk = 26   // Se recomienda un minSdk de 21 o superior
        targetSdk = 35 // Cambiado de 35 a 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 🔥 Auto-versionado dinámico
        val versionProperties = File(rootDir, "version.properties")
        val properties = Properties()

        if (versionProperties.exists()) {
            properties.load(versionProperties.inputStream())
        }

        // 🔹 Leer la versión actual
        val major = properties.getProperty("majorVersion", "1").toInt()
        val minor = properties.getProperty("minorVersion", "000").toInt()
        val build = properties.getProperty("buildNumber", "0000").toInt() + 1  // 🔥 Incrementa la versión en cada compilación

        // 🔹 Guardar la nueva versión
        properties.setProperty("majorVersion", major.toString())
        properties.setProperty("minorVersion", minor.toString())
        properties.setProperty("buildNumber", build.toString())
        versionProperties.writeText(properties.entries.joinToString("\n") { "${it.key}=${it.value}" })

        // 🔥 Configuración final de la versión
        versionCode = build
        versionName = "V$major.$minor.$build"

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
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0"
    }
}

dependencies {
    // AndroidX Core y Activity
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // ViewModel Compose para manejar ViewModels en Composable
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")

    // Dependencias adicionales
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("androidx.compose.material:material:1.3.1")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.activity:activity-compose:1.7.0") // Activity Compose
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1") // LiveData y ViewModel
    implementation("io.github.beyka:Android-TiffBitmapFactory:0.9.9.1") // Conversión a TIFF

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    // Convertidor Gson
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
}
