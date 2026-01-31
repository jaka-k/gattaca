package com.gattaca

import io.ktor.http.*
import io.ktor.server.application.*
import io.opentelemetry.instrumentation.ktor.v3_0.KtorServerTelemetry

fun Application.configureMonitoring() {
    val openTelemetry = getOpenTelemetry(serviceName = "gattaca-server")
    
    install(KtorServerTelemetry) {
        setOpenTelemetry(openTelemetry)
        capturedRequestHeaders(HttpHeaders.UserAgent, HttpHeaders.Referer)
    }
}