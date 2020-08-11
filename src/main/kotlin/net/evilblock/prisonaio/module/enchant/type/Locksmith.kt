/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.type

import net.evilblock.cubed.util.Chance
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsModule
import net.evilblock.prisonaio.module.region.Region
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

object Locksmith : AbstractEnchant("locksmith", "Locksmith", 5) {

    override val iconColor: Color
        get() = Color.AQUA

    override val textColor: ChatColor
        get() = ChatColor.AQUA

    override val menuDisplay: Material
        get() = Material.TRIPWIRE_HOOK

    override fun getCost(level: Int): Long {
        // return Math.scalb(300, level - 1);
        return (250000 + (level - 1) * 250000).toLong()
    }

    override fun onBreak(event: BlockBreakEvent, enchantedItem: ItemStack?, level: Int, region: Region) {
        for ((key, value) in readKeyPercentMap()) {
            if (Chance.percent(value * (1.0 + (level / 10)))) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate givekey to " + event.player.name + " " + key + " 1")
                sendMessage(event.player, "You have found a " + ChatColor.RED + ChatColor.BOLD + key + " Key" + ChatColor.GRAY + "!")
                break
            }
        }
    }

    private fun readKeyPercentMap(): Map<String, Double> {
        val section = EnchantsModule.config.getConfigurationSection("locksmith.key-percentages")
        return section.getKeys(false).map { it to section.getDouble(it) }.sortedBy { it.second }.shuffled().toMap()
    }

}