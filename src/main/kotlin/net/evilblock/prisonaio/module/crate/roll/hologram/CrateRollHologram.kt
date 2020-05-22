package net.evilblock.prisonaio.module.crate.roll.hologram

import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.prisonaio.module.crate.roll.CrateRoll
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class CrateRollHologram(private val crateRoll: CrateRoll) : HologramEntity(text = "${ChatColor.GREEN}Starting roll...", location = crateRoll.placedCrate.location.clone().add(0.5, -0.23, 0.5)) {

    override fun initializeData() {
        super.initializeData()

        persistent = false
    }

    override fun isVisibleToPlayer(player: Player): Boolean {
        return player.uniqueId == crateRoll.rolledBy
    }

}