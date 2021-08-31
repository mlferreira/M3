package dev.mlferreira.m3.util

import java.lang.Long

enum class GameSeries(var hex: String, name: String) {
    MARIO("000-002", "Mario"),
    YOSHIS_WOOLLY_WORLD("8", "Yoshi's Woolly World"),
    DONKEY_KONG("00C", "Donkey Kong"),
    THE_LEGEND_OF_ZELDA("10", "The Legend of Zelda"),
    BREATH_OF_THE_WILD("14", "Breath of the Wild"),
    ANIMAL_CROSSING("018-051", "Animal Crossing"),
    STAR_FOX("58", "Star Fox"),
    METROID("05C", "Metroid"),
    F_ZERO("60", "F-Zero"),
    PIKMIN("64", "Pikmin"),
    PUNCH_OUT("06C", "Punch Out"),
    WII_FIT("70", "Wii Fit"),
    KID_ICARUS("74", "Kid Icarus"),
    CLASSIC_NINTENDO("78", "Classic Nintendo"),
    MII("07C", "Mii"),
    SPLATOON("80", "Splatoon"),
    MARIO_SPORTS_SUPERSTARS("09C-09D", "Mario Sports Superstars"),
    POKEMON("190-1D4", "Pokemon"),
    KIRBY("1F0", "Kirby"),
    BOXBOY("1F4", "BoxBoy!"),
    FIRE_EMBLEM("210", "Fire Emblem"),
    XENOBLADE("224", "Xenoblade"),
    EARTHBOUND("228", "Earthbound"),
    CHIBI_ROBO("22C", "Chibi Robo"),
    SONIC("320", "Sonic"),
    BAYONETTA("324", "Bayonetta"),
    PAC_MAN("334", "Pac-Man"),
    DARK_SOULS("338", "Dark Souls"),
    MEGA_MAN("348", "Mega Man"),
    STREET_FIGHTER("34C", "Street fighter"),
    MONSTER_HUNTER("350", "Monster Hunter"),
    SHOVEL_KNIGHT("35C", "Shovel Knight"),
    FINAL_FANTASY("360", "Final Fantasy"),
    KELLOGS("374", "Kellogs"),
    METAL_GEAR_SOLID("378", "Metal Gear Solid"),
    DIABLO("38C", "Diablo");


    companion object {
        fun fromId(id: String): GameSeries? {
            if (id.length != 3) return null

            return when (Long.parseLong(id, 16).toInt()) {
                in 0..2 -> MARIO.also { it.hex = id }
                8 -> YOSHIS_WOOLLY_WORLD
                12 -> DONKEY_KONG
                16 -> THE_LEGEND_OF_ZELDA
                20 -> BREATH_OF_THE_WILD
                in 24..81 -> ANIMAL_CROSSING.also { it.hex = id }
                88 -> STAR_FOX
                92 -> METROID
                96 -> F_ZERO
                100 -> PIKMIN
                108 -> PUNCH_OUT
                112 -> WII_FIT
                116 -> KID_ICARUS
                120 -> CLASSIC_NINTENDO
                124 -> MII
                128 -> SPLATOON
                in 156..157 -> MARIO_SPORTS_SUPERSTARS.also { it.hex = id }
                in 400..468 -> POKEMON.also { it.hex = id }
                468 -> KIRBY
                500 -> BOXBOY
                528 -> FIRE_EMBLEM
                548 -> XENOBLADE
                552 -> EARTHBOUND
                556 -> CHIBI_ROBO
                800 -> SONIC
                804 -> BAYONETTA
                820 -> PAC_MAN
                824 -> DARK_SOULS
                840 -> MEGA_MAN
                844 -> STREET_FIGHTER
                848 -> MONSTER_HUNTER
                860 -> SHOVEL_KNIGHT
                864 -> FINAL_FANTASY
                884 -> KELLOGS
                888 -> METAL_GEAR_SOLID
                908 -> DIABLO
                else -> null
            }
        }
    }


}
