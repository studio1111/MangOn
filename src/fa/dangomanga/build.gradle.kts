import io.github.keiyoushi.gradle.api.ContentWarning

plugins {
    alias(kei.plugins.extension)
}

keiyoushi {
    name = "Dango Manga"
    versionCode = 1
    contentWarning = ContentWarning.SAFE
    libVersion = "1.6"

    source {
        name = "Dango Manga"
        lang = "fa"
        baseUrl = "https://dangomanga.ir"
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}
