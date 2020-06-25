/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.util

import org.bukkit.Location

class CoordsIntPair(val x: Int, val y: Int, val z: Int) {

    constructor(location: Location) : this(location.blockX, location.blockY, location.blockZ)

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is CoordsIntPair) {
            return false
        }
        return other.x == x && other.y == y && other.z == z
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }

}