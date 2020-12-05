/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.impl.money

import net.evilblock.prisonaio.module.generator.build.GeneratorBuildLevel

class MoneyBuildLevel(
    number: Int,
    schematic: String,
    nextLevelCost: Long,
    buildTime: Int
) : GeneratorBuildLevel(number, schematic, nextLevelCost, 1000L, buildTime) {

    companion object {
        val LEVELS: Array<GeneratorBuildLevel> = arrayOf(
            MoneyBuildLevel(1, "money1", 5_000L, 1_800),
            MoneyBuildLevel(2, "money2", 5_000L, 7_200),
            MoneyBuildLevel(3, "money3", 5_000L, 14_400),
            MoneyBuildLevel(4, "money4", 5_000L, 28_800),
            MoneyBuildLevel(5, "money5", 5_000L, 43_200),
            MoneyBuildLevel(6, "money6", 5_000L, 64_800),
            MoneyBuildLevel(7, "money7", 5_000L, 86_400),
            MoneyBuildLevel(8, " money8", 5_000L, 129_600),
            MoneyBuildLevel(9, "money9", 5_000L, 172_800)
        )
    }

}