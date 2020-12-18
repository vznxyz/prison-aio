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
)