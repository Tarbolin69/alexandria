package com.libreria.alexandria.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.ToJson

// Maneja el hecho de que Open Library es inconsistente en como
// devuelve la descripción de un libro. Esto lo normaliza a String.
class DescripcionAdapter {
    @ToJson
    fun toJson(value: String): String = value

    @FromJson
    fun fromJson(reader: JsonReader): String? {
        return when (reader.peek()) {
            JsonReader.Token.STRING -> reader.nextString()
            JsonReader.Token.BEGIN_OBJECT -> {
                reader.beginObject()
                var value: String? = null
                while (reader.hasNext()) {
                    when (reader.nextName()) {
                        "value" -> value = reader.nextString()
                        else -> reader.skipValue()
                    }
                }
                reader.endObject()
                value
            }
            JsonReader.Token.NULL -> {
                reader.nextNull<Unit>()
                null
            }
            else -> {
                reader.skipValue()
                null
            }
        }
    }
}
