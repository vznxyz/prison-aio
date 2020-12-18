/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.personal.data

import org.bukkit.Material

data class BlockType(val material: Material, val data: Byte, val percentage: Double) {

    companion object {
        @JvmStatic
        fun fromMap(map: Map<String, Any>): BlockType {
            return BlockType(
                    material = Material.valueOf(map["material"] as String),
                    data = (map["data"] as Int).toByte(),
                    percentage = map["percentage"] as Double
            )
        }
    }

}