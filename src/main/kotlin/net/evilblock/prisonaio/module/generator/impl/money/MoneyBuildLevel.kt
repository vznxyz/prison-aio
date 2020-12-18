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
    nextLevelCost: Long,
    buildTime: Int,
    maxMoney: Double,
    moneyPerTick: Double,
    moneyPerItem: Double
) : GeneratorBuildLevel(number, schematic, nextLevelCost, 1000L, buildTime) {

    val maxMoney: BigDecimal = BigDecimal(maxMoney)
    val moneyPerTick: BigDecimal = BigDecimal(moneyPerTick)
    val moneyPerItem: BigDecimal = BigDecimal(moneyPerItem)

    companion object {
        val LEVELS: Array<GeneratorBuildLevel> = arrayOf(
            MoneyBuildLevel(1, "money1.schematic", 5_000L, 1_800, 25_000_000_000.0, 10_000_000.0, 1_000_000_000.0),
            MoneyBuildLevel(2, "money2.schematic", 5_000L, 7_200, 50_000_000_000.0, 20_000_000.0, 2_000_000_000.0),
            MoneyBuildLevel(3, "money3.schematic", 5_000L, 14_400, 125_000_000_000.0, 50_000_000.0, 5_000_000_000.0),
            MoneyBuildLevel(4, "money4.schematic", 5_000L, 28_800, 250_000_000_000.0, 100_000_000.0, 10_000_000_000.0),
            MoneyBuildLevel(5, "money5.schematic", 5_000L, 43_200, 750_000_000_000.0, 300_000_000.0, 30_000_000_000.0),
            MoneyBuildLevel(6, "money6.schematic", 5_000L, 64_800, 1_250_000_000_000.0, 500_000_000.0, 50_000_000_000.0),
            MoneyBuildLevel(7, "money7.schematic", 5_000L, 86_400, 1_875_000_000_000.0, 750_000_000.0, 75_000_000_000.0),
            MoneyBuildLevel(8, "money8.schematic", 5_000L, 129_600, 3_125_000_000_000.0, 1_250_000_000.0, 125_000_000_000.0),
            MoneyBuildLevel(9, "money9.schematic", 5_000L, 172_800, 3_750_000_000_000.0, 1_500_000_000.0, 150_000_000_000.0)
        )
    }

}