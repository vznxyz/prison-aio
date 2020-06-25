/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region

import org.bukkit.World

class RegionCoordSet(val world: World, val x: Int, val z: Int) {

    override fun equals(other: Any?): Boolean {
        return other is RegionCoordSet && other.world == world && other.x == x && other.z == z
    }

    override fun hashCode(): Int {
        var result = world.hashCode()
        result = 31 * result + x
        result = 31 * result + z
        return result
    }

}