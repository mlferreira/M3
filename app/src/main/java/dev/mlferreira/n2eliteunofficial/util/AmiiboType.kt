package dev.mlferreira.n2eliteunofficial.util

enum class AmiiboType(var hex: String, name: String) {
    FIGURE("00", "Figure"),
    CARD("01", "Card"),
    YARN("02", "Yarn");

    companion object {
        fun fromId(id: String): AmiiboType? = values().find { it.hex == id }
        fun fromId(id: Int): AmiiboType? {
            return when (id) {
                0 -> FIGURE
                1 -> CARD
                2 -> YARN
                else -> null
            }
        }
    }

}
