plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.to.markdownnote.domain"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }
    kotlin {
        jvmToolchain(21)
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("javax.inject:javax.inject:1")

    implementation(project(":core:common"))
}
