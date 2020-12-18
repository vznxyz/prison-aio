/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.build

import net.evilblock.prisonaio.module.generator.GeneratorHandler
import net.evilblock.prisonaio.module.generator.schematic.rotate.RotatedSchematic
import net.evilblock.prisonaio.module.generator.schematic.rotate.Rotation

open class GeneratorBuildLevel(
    val number: Int,
    val schematic: String,
    val cost: Long,
    val tickInterval: Long,
    val buildTime: Int
) {

    fun getSchematic(rotation: Rotation): RotatedSchematic {
        return GeneratorHandler.getSchematic(schematic, rotation)
    }

}