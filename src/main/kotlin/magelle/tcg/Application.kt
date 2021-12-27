package magelle.tcg

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import magelle.tcg.customer.registerCustomerRoutes
import magelle.tcg.http.registerGameRoutes

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        json()
    }
    registerCustomerRoutes()
    registerGameRoutes()
}