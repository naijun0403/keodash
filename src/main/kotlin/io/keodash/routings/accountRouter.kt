package io.keodash.routings

import io.keodash.expand.toModel
import io.keodash.request.LoginRequestModel
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.accountRouting() {
    route("/database") {
        route("accounts") {
            post("/loginGJAccount.php") {
                val model = call.receiveParameters().toModel<LoginRequestModel>()

                val secret = application.environment.config.property("gmd.secret").getString()

                if (model.secret != secret) {
                    call.respondText("-1")
                    return@post
                }

                call.respondText("1,1")
            }
        }
    }
}