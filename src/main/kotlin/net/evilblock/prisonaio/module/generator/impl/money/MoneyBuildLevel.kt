/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.impl.money

import net.evilblock.prisonaio.module.generator.build.GeneratorBuildLevel
import java.math.BigDecimal

class MoneyBuildLevel(
    number: Int,
    schematic: String,
    upgradeCost: Long,
    buildTime: Int,
    maxMoney: Double,
    moneyPerTick: Double,
    moneyPerItem: Double
) : GeneratorBuildLevel(number, schematic, upgradeCost, 1000L, buildTime) {

    val maxMoney: BigDecimal = BigDecimal(maxMoney)
    val moneyPerTick: BigDecimal = BigDecimal(moneyPerTick)
    val moneyPerItem: BigDecimal = BigDecimal(moneyPerItem)

}