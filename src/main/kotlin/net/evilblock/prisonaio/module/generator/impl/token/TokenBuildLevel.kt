/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.impl.token

import net.evilblock.prisonaio.module.generator.build.GeneratorBuildLevel

class TokenBuildLevel(
    number: Int,
    schematic: String,
    nextLevelCost: Long,
    buildTime: Int
) : GeneratorBuildLevel(number, schematic, nextLevelCost, 1000L, buildTime) {

    companion object {
        val LEVELS: Array<GeneratorBuildLevel> = arrayOf(
            TokenBuildLevel(1, "token1", 5_000L, 1_800),
            TokenBuildLevel(2, "token2", 5_000L, 7_200),
            TokenBuildLevel(3, "token3", 5_000L, 14_400),
            TokenBuildLevel(4, "token4", 5_000L, 28_800),
            TokenBuildLevel(5, "token5", 5_000L, 43_200),
            TokenBuildLevel(6, "token6", 5_000L, 64_800),
            TokenBuildLevel(7, "token7", 5_000L, 86_400),
            TokenBuildLevel(8, "token8", 5_000L, 129_600),
            TokenBuildLevel(9, "token9", 5_000L, 172_800)
        )
    }

}