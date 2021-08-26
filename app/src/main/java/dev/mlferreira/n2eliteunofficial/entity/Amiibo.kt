package dev.mlferreira.n2eliteunofficial.entity

import dev.mlferreira.n2eliteunofficial.util.AmiiboSeries
import dev.mlferreira.n2eliteunofficial.util.GameSeries
import dev.mlferreira.n2eliteunofficial.util.ToyType
import dev.mlferreira.n2eliteunofficial.util.toHex
import java.lang.Long.parseLong


class Amiibo (val data: String = DUMMY) {

    init {

    }

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
    var imageUrl: String? = null
    var name: String? = null

    val head = hexId.substring(0, 8)
    val tail = hexId.substring(8, 16)

    val gameSeries: GameSeries? = GameSeries.fromId(hexId.substring(0, 3))

    val toyType: ToyType? = ToyType.fromId(hexId.substring(6, 8))

    val amiiboSeries: AmiiboSeries? = AmiiboSeries.fromId(hexId.substring(12, 14))


    val characterHex: String = hexId.substring(3, 4)

    val characterIdHex: String = hexId.substring(0, 4)

    val characterVariantHex: String = hexId.substring(4, 6)

    val subCharacterHex: String = hexId.substring(0, 6)

    val amiiboNoHex: String = hexId.substring(8, 12)



    constructor(bArr: ByteArray) : this(bArr.toHex())

    override fun toString() = hexId

    companion object {
        const val DUMMY = "FFFFFFFFFFFFFFFF"
        const val API_URL = "https://www.amiiboapi.com/api/amiibo/"
    }
}



