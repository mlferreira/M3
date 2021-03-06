package dev.mlferreira.m3.entity

class Bank(
    val amiibo: Amiibo = Amiibo(),
    val tagId: ByteArray = ByteArray(8)
) {
    fun getUid() = tagId.joinToString("") { "%02x".format(it) }

    companion object {
        const val MAX_BANKS = 200
    }
}