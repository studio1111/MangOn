import io.github.keiyoushi.gradle.api.ContentWarning

plugins {
    alias(kei.plugins.extension)
}

keiyoushi {
    name = "ManhwaChi"
    versionCode = 7
    contentWarning = ContentWarning.SAFE
    libVersion = "1.6"

    source {
        name = "ManhwaChi"
        lang = "fa"
        baseUrl = "https://manhwachi.ir"
    }
}

dependencies {
    testImplementation(libs.junit)
}
