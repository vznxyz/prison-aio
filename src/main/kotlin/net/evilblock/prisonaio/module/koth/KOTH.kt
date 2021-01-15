/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.koth

import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.region.bitmask.BitmaskRegion
import net.evilblock.prisonaio.module.region.bitmask.RegionBitmask
import java.lang.reflect.Type

class KOTH(id: String) : BitmaskRegion(id) {

    var captureCuboid: Cuboid? = null

    override fun initializeData() {
        super.initializeData()

        if (!hasBitmask(RegionBitmask.DANGER_ZONE)) {
            addBitmask(RegionBitmask.DANGER_ZONE)
        }
    }

    override fun getAbstractType(): Type {
        return KOTH::class.java
    }

}