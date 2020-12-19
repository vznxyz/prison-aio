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

}