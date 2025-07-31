package org.app.inflo.utils

import kotlinx.serialization.json.Json

val AppJson = Json {
    // Setting this true means if json contains any unknown keys, it will not be treated as an
    // error and will simply ignore those keys
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
    // Setting this false means if variable is nullable, and any key is not present in json
    // it will set the value for that variable to null
    explicitNulls = false
    // Setting this to true, to serialize default values of request api models,
    // otherwise only those values are serialized which are passed during model creation.
    encodeDefaults = true
}