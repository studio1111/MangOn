import io.github.keiyoushi.gradle.api.ContentWarning

plugins {
    alias(kei.plugins.extension)
}

keiyoushi {
    name = "Meast"
    versionCode = 1
    contentWarning = ContentWarning.SAFE
    libVersion = "1.6"

    source {
        name = "Meast"
        lang = "fa"
        baseUrl = "https://meast.ir"
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}
