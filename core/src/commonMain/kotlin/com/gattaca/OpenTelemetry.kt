package com.gattaca

import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk
import io.opentelemetry.semconv.ServiceAttributes

fun getOpenTelemetry(serviceName: String): OpenTelemetry {
    // Configuration for Grafana stack (Tempo/Loki/Prometheus)
    // We rely on OTLP as the standard exporter.
    // Ensure environment variables like OTEL_EXPORTER_OTLP_ENDPOINT are set in the environment.
    
    return AutoConfiguredOpenTelemetrySdk.builder()
        .addResourceCustomizer { oldResource, _ ->
            oldResource.toBuilder()
                .put(ServiceAttributes.SERVICE_NAME, serviceName)
                .build()
        }
        .build()
        .openTelemetrySdk
}