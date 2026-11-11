package eu.kanade.tachiyomi.extension.fa.manhwachi

import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ManhwaChiImageUrlTest {
    private val sourceUrl = "https://manhwachi.ir".toHttpUrl()

    @Test
    fun resolvesRelativeSignedImageUrl() {
        val result = resolveReaderImageUrl(
            "/chapter-image/manhwas/the-forgotten-field/CH39/001.webp?token=abc&expires=123",
            sourceUrl,
        )

        assertEquals(
            "https://manhwachi.ir/chapter-image/manhwas/the-forgotten-field/CH39/001.webp?token=abc&expires=123",
            result.toString(),
        )
    }

    @Test
    fun acceptsAbsoluteSameOriginImageUrl() {
        val result = resolveReaderImageUrl(
            "https://manhwachi.ir/chapter-image/manhwas/the-forgotten-field/CH39/002.webp",
            sourceUrl,
        )

        assertEquals(
            "https://manhwachi.ir/chapter-image/manhwas/the-forgotten-field/CH39/002.webp",
            result.toString(),
        )
    }

    @Test
    fun rejectsExternalImageHost() {
        assertNull(
            resolveReaderImageUrl(
                "https://images.example/chapter-image/manhwas/title/CH1/001.webp",
                sourceUrl,
            ),
        )
    }

    @Test
    fun rejectsProtocolRelativeExternalHost() {
        assertNull(
            resolveReaderImageUrl(
                "//images.example/chapter-image/manhwas/title/CH1/001.webp",
                sourceUrl,
            ),
        )
    }

    @Test
    fun rejectsInsecureScheme() {
        assertNull(
            resolveReaderImageUrl(
                "http://manhwachi.ir/chapter-image/manhwas/title/CH1/001.webp",
                sourceUrl,
            ),
        )
    }

    @Test
    fun rejectsNonReaderPath() {
        assertNull(resolveReaderImageUrl("/api/get-chapter-images", sourceUrl))
    }

    @Test
    fun rejectsCredentialsAndAlternatePorts() {
        assertNull(
            resolveReaderImageUrl(
                "https://user@manhwachi.ir/chapter-image/manhwas/title/CH1/001.webp",
                sourceUrl,
            ),
        )
        assertNull(
            resolveReaderImageUrl(
                "https://manhwachi.ir:8443/chapter-image/manhwas/title/CH1/001.webp",
                sourceUrl,
            ),
        )
    }
}
