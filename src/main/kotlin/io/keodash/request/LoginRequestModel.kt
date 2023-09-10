package io.keodash.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestModel(
    val udid: String, // user device id
    val userName: String,
    val password: String,
    val sID: String, // session id?
    val secret: String, // secret key
)
