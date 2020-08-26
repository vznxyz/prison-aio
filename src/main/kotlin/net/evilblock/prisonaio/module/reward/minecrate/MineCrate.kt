/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.minecrate

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.prisonaio.module.reward.minecrate.reward.MineCrateRewardSet
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import java.util.*

class MineCrate(val location: Location, val owner: UUID, val rewardSet: MineCrateRewardSet) {

    val spawnedAt = System.currentTimeMillis()
    val hologram: HologramEntity = HologramEntity(text = "", location = location.clone().add(0.5, 0.0, 0.5))

    init {
        val ownerName = Cubed.instance.uuidCache.name(owner)

        val lines = MineCrateHandler.getHologramLines().map {
            it.replace("{ownerName}", ownerName)
        }

        hologram.initializeData()
        hologram.persistent = false
        hologram.updateLines(lines)

        EntityManager.trackEntity(hologram)

        Tasks.sync {
            location.block.type = Material.ENDER_CHEST
            location.block.state.update()
        }
    }

    fun destroy(enforceSync: Boolean = true) {
        hologram.destroyForCurrentWatchers()
        EntityManager.forgetEntity(hologram)

        if (enforceSync && !Bukkit.isPrimaryThread()) {
            Tasks.sync {
                location.block.type = Material.AIR
                location.block.state.update()
            }
        } else {
            location.block.type = Material.AIR
            location.block.state.update()
        }
    }

}