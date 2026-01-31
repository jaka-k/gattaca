package com.gattaca.public

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.opentelemetry.instrumentation.ktor.v3_0.KtorClientTelemetry
import com.gattaca.getOpenTelemetry

/**
 * Configuration for the public-facing client.
 * Focuses on standard HTTP protocols, public telemetry, and standard serialization.
 */
fun HttpClientConfig<*>.configurePublicClient() {
    val openTelemetry = getOpenTelemetry(serviceName = "gattaca-public-client")
    
    install(KtorClientTelemetry) {
        setOpenTelemetry(openTelemetry)
        capturedRequestHeaders(HttpHeaders.Accept, HttpHeaders.ContentType)
    }

    install(ContentNegotiation) {
        json()
    }
}