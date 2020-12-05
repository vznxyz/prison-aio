/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.impl.key

import net.evilblock.prisonaio.module.generator.build.GeneratorBuildLevel

class KeyBuildLevel(
    number: Int,
    schematic: String,
    nextLevelCost: Long,
    tickInterval: Long,
    buildTime: Int,
    val maxKeys: Int,
    val keys: List<KeyChance>
) : GeneratorBuildLevel(number, schematic, nextLevelCost, tickInterval, buildTime) {

    data class KeyChance(val key: String, val chance: Double)

    companion object {
        val LEVELS: Array<GeneratorBuildLevel> = arrayOf(
            KeyBuildLevel(1, "key1.schematic", 5000L, 180_000L, 1_800, 5, listOf(KeyChance("Common", 80.0), KeyChance("Rare", 15.0), KeyChance("Legendary", 5.0))),
            KeyBuildLevel(2, "key2.schematic", 15_000L, 120_000L, 14_400, 7, listOf()),
            KeyBuildLevel(3, "key3.schematic", 50_000L,  90_000L, 64_800, 8, listOf()),
            KeyBuildLevel(4, "key4.schematic", 250_000L, 60_000L, 86_400, 10, listOf()),
            KeyBuildLevel(5, "key5.schematic", 1_000_000L, 30_000L, 172_800, 15, listOf())
        )
    }

}