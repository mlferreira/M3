package dev.mlferreira.n2eliteunofficial.nfc

enum class N2TagOpCode(val byte: Byte) {
    GET_VERSION(0x60), // 96
    READ(0x30), // 48
    FAST_READ(0x3A), // 58
    WRITE(0xA2.toByte()), // -94
    READ_CNT(0x39), // 57
    PWD_AUTH(0x1B), // 27
    READ_SIG(0x3C), // 60
    SECTOR_SELECT(0xC2.toByte()), // -62
    READ_TT_STATUS(0xA4.toByte()),
    MFULC_AUTH1(0x1A.toByte()),
    MFULC_AUTH2(0xAF.toByte()),

    // AMIIBO
    AMIIBO_ACTIVATE_BANK((-89).toByte()),
    AMIIBO_FAST_READ(59),
    AMIIBO_FAST_WRITE((-82).toByte()),
    AMIIBO_GET_STATUS(85),
    AMIIBO_LOCK(70),
    AMIIBO_READ_SIG(67),
    AMIIBO_SET_BANKCOUNT((-87).toByte()),
    AMIIBO_UNLOCK_1(68),
    AMIIBO_UNLOCK_2(69),
    AMIIBO_WRITE((-91).toByte());

    companion object {
        fun fromByte(byteValue: Byte): N2TagOpCode? = values().firstOrNull { it.byte == byteValue }
    }
}