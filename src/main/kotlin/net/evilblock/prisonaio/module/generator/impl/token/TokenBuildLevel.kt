/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.impl.token

import net.evilblock.prisonaio.module.generator.build.GeneratorBuildLevel
import java.math.BigDecimal

class TokenBuildLevel(
    number: Int,
    schematic: String,
    upgradeCost: Long,
    buildTime: Int,
    maxTokens: Double,
    tokensPerTick: Double,
    tokensPerItem: Double
) : GeneratorBuildLevel(number, schematic, upgradeCost, 1000L, buildTime) {

    val maxTokens: BigDecimal = BigDecimal(maxTokens)
    val tokensPerTick: BigDecimal = BigDecimal(tokensPerTick)
    val tokensPerItem: BigDecimal = BigDecimal(tokensPerItem)

}