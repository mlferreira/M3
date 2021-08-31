package dev.mlferreira.m3.entity

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.gson.JsonObject
import dev.mlferreira.m3.util.AmiiboSeries
import dev.mlferreira.m3.util.GameSeries
import dev.mlferreira.m3.util.AmiiboType
import dev.mlferreira.m3.util.toHex
import org.json.JSONObject


class Amiibo (
    val data: String = DUMMY,
    val context: Context? = null
) {

    var id: String = data
        set(value) {
            var hex = value
            if (hex.startsWith("0x", ignoreCase = true)) {
                hex = hex.removePrefix("0x")
            }
            if (hex.length < 16) {
                while (hex.length < 16) {
                    hex += "F"
                }
            } else if (hex.length > 16) {
                hex = hex.substring(0, 16)
            }
            field = hex
        }

    var tampered = false

    var name: String? = null

    var imageBytes: ByteArray? = null
    var image: Bitmap? = null

    val gameSeries: GameSeries? = GameSeries.fromId(id.substring(0, 3))
    val characterId: String = id.substring(3, 4)
    var character: String? = null
    val variation: String = id.substring(4, 6)
    val type: AmiiboType? = AmiiboType.fromId(id.substring(6, 8))
    val amiiboModelNumber: String = id.substring(8, 12)
    val amiiboSeries: AmiiboSeries? = AmiiboSeries.fromId(id.substring(12, 14))



    constructor(bArr: ByteArray, context: Context?) : this(bArr.toHex(), context)

    constructor(jsonObject: JsonObject) : this(jsonObject.getAsJsonPrimitive("id").asString) {
        Log.d(this::class.simpleName, "[json constructor] $id")
        this.name = jsonObject.getAsJsonPrimitive("name").asString
        this.character = jsonObject.getAsJsonPrimitive("character").asString
    }

    constructor(
        id: String,
        name: String,
        gameSeries: String,
        character: String,
        type: String,
        amiiboSeries: String,
    ) : this (id) {
        this.name = name
        this.character = character
    }

    override fun toString() = id

    fun toJsonString(): String? {
        if (name == null) return null

        val map = mapOf(
            "id" to "id",
            "name" to name,
            "gameSeries" to gameSeries,
            "character" to character,
            "type" to type,
            "amiiboSeries" to amiiboSeries,
            )

        return JSONObject(map).toString()
    }

    companion object {
        const val DUMMY = "FFFFFFFFFFFFFFFF"
        const val API_URL = "https://raw.githubusercontent.com/mlferreira/M3/main/amiibos/"
        const val DATA_EXTENSION = ".json";
        const val IMAGE_EXTENSION = ".png";
    }

}



