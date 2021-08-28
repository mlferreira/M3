package dev.mlferreira.n2eliteunofficial.entity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import dev.mlferreira.n2eliteunofficial.NFCApp
import dev.mlferreira.n2eliteunofficial.util.AmiiboSeries
import dev.mlferreira.n2eliteunofficial.util.GameSeries
import dev.mlferreira.n2eliteunofficial.util.ToyType
import dev.mlferreira.n2eliteunofficial.util.toHex
import java.io.File
import java.lang.Long.parseLong
import java.nio.ByteBuffer


class Amiibo (
    val data: String = DUMMY,
    val context: Context? = null
) {

    var hexId: String = data
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

    val head = hexId.substring(0, 8)
    val tail = hexId.substring(8, 16)

    var fileName: String = "$head-$tail"

    val gameSeries: GameSeries? = GameSeries.fromId(hexId.substring(0, 3))

    val toyType: ToyType? = ToyType.fromId(hexId.substring(6, 8))

    val amiiboSeries: AmiiboSeries? = AmiiboSeries.fromId(hexId.substring(12, 14))

    val characterHex: String = hexId.substring(3, 4)

    val characterIdHex: String = hexId.substring(0, 4)

    val characterVariantHex: String = hexId.substring(4, 6)

    val subCharacterHex: String = hexId.substring(0, 6)

    val amiiboNoHex: String = hexId.substring(8, 12)



    constructor(bArr: ByteArray, context: Context?) : this(bArr.toHex(), context)

    init {
        if(!hexId.equals(DUMMY, true)) {
            fetchData()
            fetchImage()
        }
    }

    fun fetchData() {
        if (context == null) return

        val dataFile = File(context.cacheDir, "$fileName$DATA_EXTENSION")

        if (dataFile.exists()) {
            getDataFromJson(JsonParser.parseString(dataFile.readText()))
        } else {
            val queue = Volley.newRequestQueue(context)

            val stringRequest = StringRequest(
                Request.Method.GET,
                "$API_URL?id=$hexId",
                { response ->
                    getDataFromJson(JsonParser.parseString(response))
                },
                { e ->  }
            )

            queue.add(stringRequest)
        }

    }

    private fun getDataFromJson(json: JsonElement) {

        var temp: JsonElement = if (json.isJsonArray) {
            json.asJsonArray.first().asJsonObject
        } else {
            json.asJsonObject
        }

        if (json.asJsonObject.has("amiibo")) {
            temp = temp.asJsonObject.get("amiibo")
        }

        if (json.isJsonArray) {
            temp = temp.asJsonArray.first().asJsonObject
        }

        val data = temp.asJsonObject

        name = data.getAsJsonPrimitive("name")?.asString
            ?: data.getAsJsonPrimitive("character")?.asString ?: "?"

        if (context != null) {
           val dataFile = File(context.filesDir, "$fileName$DATA_EXTENSION")
            dataFile.writeText(data.toString())
        }
    }

    private fun fetchImage() {
        if (imageBytes != null || context == null) return

        val imgFile = File(context.filesDir, "$fileName$IMAGE_EXTENSION")

        if (imgFile.exists()) {
            imageBytes = imgFile.readBytes()
            image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes!!.size)
        } else {
            val queue = Volley.newRequestQueue(context)

            val stringRequest = ImageRequest(
                IMAGE_URL + fileName + IMAGE_EXTENSION,
                { img ->
                    val size: Int = img.rowBytes * img.height
                    val byteBuffer: ByteBuffer = ByteBuffer.allocate(size)
                    img.copyPixelsToBuffer(byteBuffer)
                    imageBytes = imgFile.readBytes()

                    imgFile.writeBytes(imageBytes!!)
                    image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes!!.size)
                },
                0,
                0,
                ImageView.ScaleType.CENTER_INSIDE,
                Bitmap.Config.RGB_565,
                {  }
            )

            queue.add(stringRequest)
        }

    }


    override fun toString() = hexId

    companion object {
        const val DUMMY = "FFFFFFFFFFFFFFFF"
        const val API_URL = "https://www.amiiboapi.com/api/amiibo/"
        const val IMAGE_URL = "https://raw.githubusercontent.com/N3evin/AmiiboAPI/master/images/icon_";
        const val IMAGE_EXTENSION = ".png";
        const val DATA_EXTENSION = ".json";
    }

}



