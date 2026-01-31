package com.gattaca

import com.gattaca.configs.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val deps = configureDatabases()
    configureSerialization()
    configureMonitoring()
    configureExceptionHandling()
    configureSecurity()
    configureHTTP()
    configureRouting(deps)
}