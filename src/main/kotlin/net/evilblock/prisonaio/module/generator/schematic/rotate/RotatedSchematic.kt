/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.schematic.rotate

import net.evilblock.prisonaio.module.generator.schematic.SchematicBlock
import org.bukkit.util.Vector
import kotlin.experimental.and

class RotatedSchematic(
    val rotation: Rotation,
    val area: Vector,
    val blocks: Array<SchematicBlock>,
    var villager: Vector,
    var villagerYaw: Float
) {

    fun setVillagerYaw(data: Byte) {
        when ((data and 3).toInt()) {
            1 -> villagerYaw = 90f
            2 -> villagerYaw = 180f
            3 -> villagerYaw = 270f
        }

        when (rotation.getOpposite()) {
            Rotation.EAST -> villagerYaw += 90f
            Rotation.SOUTH -> villagerYaw += 180f
            Rotation.WEST -> villagerYaw += 270f
            else -> {}
        }

        villagerYaw %= 360f
    }

}