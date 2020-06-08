package net.evilblock.prisonaio.module.achievement.type

import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.prisonaio.module.achievement.Achievement
import net.evilblock.prisonaio.module.rank.event.AsyncPlayerPrestigeEvent
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class PrestigeAchievement(private val prestige: Int) : Achievement("prestige-$prestige") {

    override fun getDisplayName(): String {
        return "Reach Prestige $prestige"
    }

    override fun getDisplayIcon(): ItemStack {
        return ItemBuilder.of(Material.MAGMA).build()
    }

    override fun onPlayerPrestige(event: AsyncPlayerPrestigeEvent) {
        if (event.to == prestige) {
            completedAchievement(event.player)
        }
    }

}