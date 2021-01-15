/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.util

import org.bukkit.World

class CoordSet3D(val world: World, val x: Int, val y: Int, val z: Int) {

    override fun equals(other: Any?): Boolean {
        return other is CoordSet3D && other.world == world && other.x == x && other.y == y && other.z == z
    }

    override fun hashCode(): Int {
        var result = world.hashCode()
        result = 31 * result + x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }

}