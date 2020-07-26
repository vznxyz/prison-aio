/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.impl.safezone

import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.region.Region
import org.bukkit.ChatColor

class SafeZoneRegion : Region {

    override fun getRegionName(): String {
        return "${ChatColor.GREEN}${ChatColor.BOLD}SafeZone"
    }

    override fun getCuboid(): Cuboid? {
        return null
    }

    override fun is3D(): Boolean {
        return false
    }

    override fun getBreakableCuboid(): Cuboid? {
        return null
    }

    override fun resetBreakableCuboid() {

    }

    override fun supportsAbilityEnchants(): Boolean {
        return false
    }

    override fun supportsPassiveEnchants(): Boolean {
        return true
    }

    override fun supportsRewards(): Boolean {
        return false
    }

}