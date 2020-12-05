/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.schematic

import org.bukkit.Material
import org.bukkit.util.Vector

data class SchematicBlock(
    val type: Int,
    var data: Byte,
    var vector: Vector
) {

    fun getMaterial(): Material {
        return if (type < 0) {
            Material.AIR
        } else {
            Material.getMaterial(type)
        }
    }

}