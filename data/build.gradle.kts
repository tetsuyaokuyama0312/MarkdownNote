plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.to.markdownnote.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }
    kotlin {
        jvmToolchain(21)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.hilt.compiler)
    ksp(libs.kotlin.metadata.jvm)

    implementation(project(":domain"))
    implementation(project(":core:common"))
}
