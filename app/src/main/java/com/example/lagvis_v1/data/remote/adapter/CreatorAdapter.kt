// data/remote/adapters/CreatorAdapter.kt
package com.example.lagvis_v1.data.remote.adapters

import com.google.gson.*
import java.lang.reflect.Type

class CreatorAdapter : JsonDeserializer<String?> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, ctx: JsonDeserializationContext?): String? {
        if (json == null || json.isJsonNull) return null
        return when {
            json.isJsonArray -> json.asJsonArray.joinToString(", ") { it.asString }
            json.isJsonPrimitive && json.asJsonPrimitive.isString -> json.asString
            else -> json.toString() // fallback por si viniera raro
        }
    }
}
