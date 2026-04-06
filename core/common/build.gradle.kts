plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.to.markdownnote.core.common"
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

    // CommonMark (Markdown parser)
    implementation(libs.commonmark)
    implementation(libs.commonmark.ext.strikethrough)
    implementation(libs.commonmark.ext.tables)

    testImplementation(libs.junit)
}
