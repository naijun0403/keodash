package io.keodash

import io.keodash.plugins.*
import io.keodash.routings.accountRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureTemplating()
    configureDatabases()
    configureSockets()
    configureRouting()

    routing {
        accountRouting()
    }
}
