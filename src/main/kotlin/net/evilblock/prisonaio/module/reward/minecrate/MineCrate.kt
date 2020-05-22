package net.evilblock.prisonaio.module.reward.minecrate

import net.evilblock.cubed.Cubed
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.reward.minecrate.reward.MineCrateRewardSet
import org.bukkit.Location
import org.bukkit.Material
import java.util.*

class MineCrate(val location: Location, val owner: UUID, val rewardSet: MineCrateRewardSet) {

    val spawnedAt = System.currentTimeMillis()
    private val hologram: HologramEntity = HologramEntity(text = "", location = location.clone().add(0.5, 0.0, 0.5))

    init {
        val ownerName = Cubed.instance.uuidCache.name(owner)

        val lines = MineCrateHandler.getHologramLines().map {
            it.replace("{ownerName}", ownerName)
        }

        hologram.initializeData()
        hologram.persistent = false
        hologram.updateLines(lines)

        EntityManager.trackEntity(hologram)

        PrisonAIO.instance.server.scheduler.runTask(PrisonAIO.instance) {
            location.block.type = Material.ENDER_CHEST
            location.block.state.update()
        }
    }

    fun destroy(enforceSync: Boolean = true) {
        hologram.destroyForCurrentWatchers()
        EntityManager.forgetEntity(hologram)

        if (enforceSync) {
            PrisonAIO.instance.server.scheduler.runTask(PrisonAIO.instance) {
                location.block.type = Material.AIR
                location.block.state.update()
            }
        } else {
            location.block.type = Material.AIR
            location.block.state.update()
        }
    }

}