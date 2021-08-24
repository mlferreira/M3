package dev.mlferreira.n2eliteunofficial

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.*
import java.lang.Long.parseLong


class Amiibo (
    val hexId: String = DUMMY_ID
//    val id: Long = parseLong(DUMMY_ID, 16)
) {

    var tampered = false
    var imageUrl: String? = null
    var name: String = "?"

    val gameSeriesHex: String = hexId.substring(0, 3)
    val gameSeriesId: Int = parseLong(gameSeriesHex, 16).toInt()

    val characterHex: String = hexId.substring(3, 4)
    val characterNumber: Int = parseLong(characterHex, 16).toInt()

    val characterIdHex: String = hexId.substring(0, 4)
    val characterId: Int = parseLong(characterIdHex, 16).toInt()

    val characterVariantHex: String = hexId.substring(4, 6)
    val characterVariantId: Int = parseLong(characterVariantHex, 16).toInt()

    val subCharacterHex: String = hexId.substring(0, 6)
    val subCharacterId: Int = parseLong(subCharacterHex, 16).toInt()

    val toyTypeHex: String = hexId.substring(6, 8)
    val toyTypeId: Int = parseLong(toyTypeHex, 16).toInt()

    val amiiboNoHex: String = hexId.substring(8, 12)
    val amiiboNoId: Int = parseLong(amiiboNoHex, 16).toInt()

    val amiiboSeriesHex: String = hexId.substring(12, 14)
    val amiiboSeriesId: Int = parseLong(amiiboSeriesHex, 16).toInt()


    //    constructor(hexId: String = DUMMY_ID) : this(parseLong(hexId, 16))

    constructor(id: Long) : this(id.toString(16))

    constructor(bArr: Array<Byte>) : this(bArr.joinToString("") { "%02x".format(it) })

    constructor(bArr: ByteArray) : this(bArr.joinToString("") { "%02x".format(it) })

    override fun toString() = hexId

    companion object {
        const val DUMMY_ID = "FFFFFFFFFFFFFFFF"
        const val API_URL = "https://www.amiiboapi.com/api/amiibo/"
    }
}