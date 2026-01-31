package com.gattaca

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val repos = configureDatabases()
    configureSerialization()
    configureMonitoring()
    configureExceptionHandling()
    configureSecurity()
    configureHTTP()
    configureRouting(repos)
}