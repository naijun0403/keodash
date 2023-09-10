package io.keodash.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestModel(
    val userName: String,
    val password: String,
    val email: String,
    val secret: String
)
