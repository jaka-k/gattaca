package com.gattaca.internal

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.opentelemetry.instrumentation.ktor.v3_0.KtorClientTelemetry
import com.gattaca.getOpenTelemetry

/**
 * Configuration for Inter-process Communication (IPC).
 * Optimized for internal service-to-service calls.
 */
fun HttpClientConfig<*>.configureInternalClient() {
    val openTelemetry = getOpenTelemetry(serviceName = "gattaca-internal-ipc")
    
    install(KtorClientTelemetry) {
        setOpenTelemetry(openTelemetry)
        // Internal clients might want to trace more detailed headers
        capturedRequestHeaders(
            HttpHeaders.Accept, 
            HttpHeaders.ContentType,
            "X-Internal-Service-ID"
        )
    }

    install(ContentNegotiation) {
        json()
    }
}
