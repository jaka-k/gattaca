package com.gattaca

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val cityService = configureDatabases()
    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureHTTP()
    configureRouting(cityService)
}