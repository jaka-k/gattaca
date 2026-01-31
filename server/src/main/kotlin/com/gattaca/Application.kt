package com.gattaca

import com.gattaca.config.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val deps = configureDatabases()
        configureSerialization()
        configureMonitoring()
        configureExceptionHandling()
        configureSecurity(deps.sessionRepo)
        configureHTTP()
        configureRouting(deps)
    }
    