package io.kamel.tests

import io.ktor.utils.io.*


actual val resourceImage: ByteReadChannel
    get() {
        val bytes = MR.files.Compose.readText().encodeToByteArray()
        return ByteReadChannel(bytes)
    }

actual val svgImage: ByteReadChannel
    get() {
        val bytes = MR.files.Kotlin.readText().encodeToByteArray()
        return ByteReadChannel(bytes)
    }
