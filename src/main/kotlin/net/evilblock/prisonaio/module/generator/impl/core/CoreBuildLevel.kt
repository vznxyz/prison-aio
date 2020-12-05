/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.impl.core

import net.evilblock.prisonaio.module.generator.build.GeneratorBuildLevel

class CoreBuildLevel(
    number: Int,
    schematic: String,
    upgradeCost: Long,
    buildTime: Int,
    val maxBuilds: Int,
    val maxKeyLevel: Int,
    val maxMoneyLevel: Int,
    val maxTokenLevel: Int
) : GeneratorBuildLevel(number, schematic, upgradeCost, 1000L, buildTime) {

    companion object {
        val LEVELS: Array<GeneratorBuildLevel> = arrayOf(
            CoreBuildLevel(1, "core1.schematic", 5000L, 1_800, 2, 1, 2, 2),
            CoreBuildLevel(2, "core2.schematic", 10_000L, 7_200, 3, 2, 3, 3),
            CoreBuildLevel(3, "core3.schematic", 50_000L, 14_400, 4, 2, 4, 4),
            CoreBuildLevel(4, "core4.schematic", 100_000L, 28_800, 5, 3, 5, 5),
            CoreBuildLevel(5, "core5.schematic", 250_000L, 43_200, 6, 3, 6, 6),
            CoreBuildLevel(6, "core6.schematic", 500_000L, 86_400, 7, 3, 7, 7),
            CoreBuildLevel(7, "core7.schematic", 1_000_000L, 172_800, 8, 4, 7, 7),
            CoreBuildLevel(8, "core8.schematic", 5_000_000L, 259_200, 10, 4, 8, 8),
            CoreBuildLevel(9, "core9.schematic", 50_000_000L, 345_600, 10, 5, 9, 9)
        )
    }

}