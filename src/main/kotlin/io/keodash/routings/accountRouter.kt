package io.keodash.routings

import io.keodash.databases.UserDto
import io.keodash.expand.toModel
import io.keodash.helper.CoreHelper
import io.keodash.request.LoginRequestModel
import io.keodash.request.RegisterRequestModel
import io.keodash.user.UserPermission
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.accountRouting() {
    val userService = CoreHelper.userServiceGetter?.invoke() ?: throw Exception("UserService not initialized")

    route("/database") {
        route("/accounts") {
            post("/registerGJAccount.php") {
                val model = call.receiveParameters().toModel<RegisterRequestModel>()

                val secret = application.environment.config.property("gmd.secret").getString()

                if (model.secret != secret) {
                    call.respondText("-1")
                    return@post
                }

                if (model.userName.length > 20) {
                    call.respondText("-4")
                    return@post
                }

                val emailResult = userService.readByEmail(model.email)

                if (emailResult != null) {
                    call.respondText("-3")
                    return@post
                }

                val userNameResult = userService.readByName(model.userName)

                if (userNameResult != null) {
                    call.respondText("-2")
                    return@post
                }

                userService.create(
                    UserDto(
                        name = model.userName,
                        password = model.password,
                        email = model.email,
                        permission = UserPermission.USER
                    )
                )

                call.respondText("1")
            }

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