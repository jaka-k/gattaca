plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.serialization)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            api(libs.opentelemetry.sdk.extension.autoconfigure)
            api(libs.opentelemetry.semconv)
            api(libs.opentelemetry.exporter.otlp)
            api(libs.opentelemetry.ktor)
            api(libs.ktor.serialization.kotlinx.json)
        }
        jvmMain.dependencies {
            implementation(libs.postgresql)
            implementation(libs.h2)
        }
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")
    }
}
