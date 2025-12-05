package com.coinsocket.data.remote.dto

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonPrimitive

fun parseKlines(jsonArray: JsonArray): List<Double> {
    return jsonArray.map {
        val candle = it as JsonArray
        candle[4].jsonPrimitive.content.toDoubleOrNull() ?: 0.0
    }
}