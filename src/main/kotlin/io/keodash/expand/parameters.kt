package io.keodash.expand

import io.ktor.http.*
import kotlinx.serialization.json.*

inline fun Parameters.toJsonObject(): JsonObject {
    return buildJsonObject {
        for (key in this@toJsonObject.names()) {
            put(key, this@toJsonObject[key])
        }
    }
}

inline fun <reified T> Parameters.toModel(): T {
    return Json.decodeFromJsonElement(this.toJsonObject())
}