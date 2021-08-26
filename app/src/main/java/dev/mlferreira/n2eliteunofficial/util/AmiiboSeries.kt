package dev.mlferreira.n2eliteunofficial.util

enum class AmiiboSeries(var hex: String, name: String) {
    SUPER_SMASH_BROS("00", "Super Smash Bros"),
    SUPER_MARIO("01", "Super Mario"),
    CHIBI_ROBO("02", "Chibi Robo"),
    YOSHIS_WOOLLY_WORLD("03", "Yoshi's Woolly World"),
    SPLATOON("04", "Splatoon"),
    ANIMAL_CROSSING("05", "Animal Crossing"),
    EIGHT_BIT_MARIO("06", "8-Bit Mario"),
    SKYLANDERS("07", "Skylanders"),
    THE_LEGEND_OF_ZELDA("09", "The Legend of Zelda"),
    SHOVEL_KNIGHT("0A", "Shovel Knight"),
    KIRBY("0C", "Kirby"),
    POKEMON("0D", "Pokemon"),
    MARIO_SPORTS_SUPERSTARS("0E", "Mario Sports Superstars"),
    MONSTER_HUNTER("0F", "Monster Hunter"),
    BOXBOY("10", "BoxBoy!"),
    PIKMIN("11", "Pikmin"),
    FIRE_EMBLEM("12", "Fire Emblem"),
    METROID("13", "Metroid"),
    OTHERS("14", "Others"),
    MEGA_MAN("15", "Mega Man"),
    DIABLO("16", "Diablo");

    companion object {
        fun fromId(id: String): AmiiboSeries? = values().find { it.hex == id }
        fun fromId(id: Int): AmiiboSeries? {
            return when (id) {
                0 -> SUPER_SMASH_BROS
                1 -> SUPER_MARIO
                2 -> CHIBI_ROBO
                3 -> YOSHIS_WOOLLY_WORLD
                4 -> SPLATOON
                5 -> ANIMAL_CROSSING
                6 -> EIGHT_BIT_MARIO
                7 -> SKYLANDERS
                9 -> THE_LEGEND_OF_ZELDA
                10 -> SHOVEL_KNIGHT
                12 -> KIRBY
                13 -> POKEMON
                14 -> MARIO_SPORTS_SUPERSTARS
                15 -> MONSTER_HUNTER
                16 -> BOXBOY
                17 -> PIKMIN
                18 -> FIRE_EMBLEM
                19 -> METROID
                20 -> OTHERS
                21 -> MEGA_MAN
                22 -> DIABLO
                else -> null
            }
        }
    }

}