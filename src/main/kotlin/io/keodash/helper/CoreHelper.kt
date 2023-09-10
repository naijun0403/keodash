package io.keodash.helper

import io.keodash.databases.UserService
import io.keodash.plugins.connectToPostgres
import io.ktor.server.application.*

object CoreHelper {

    var userServiceGetter: (() -> UserService)? = null

    fun init(application: Application) {
        val userService = UserService(
            application.connectToPostgres()
        )

        userServiceGetter = { userService }
    }

}