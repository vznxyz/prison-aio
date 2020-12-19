/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.impl.key

import net.evilblock.cubed.util.Chance
import net.evilblock.prisonaio.module.generator.build.GeneratorBuildLevel

class KeyBuildLevel(
    number: Int,
    schematic: String,
    upgradeCost: Long,
    tickInterval: Long,
    buildTime: Int,
    val maxKeys: Int,
    val keys: List<KeyChance>
) : GeneratorBuildLevel(number, schematic, upgradeCost, tickInterval, buildTime) {

    fun pickRandomKey(): KeyChance {
        return Chance.weightedPick(keys) { it.chance }
    }

    data class KeyChance(val key: String, val chance: Double)

}