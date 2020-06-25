/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.type

import net.evilblock.cubed.util.TimeUtil.formatIntoDetailedString
import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsModule
import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

object Exporter : AbstractEnchant("exporter", "Exporter", 3) {

    private val useCooldown: MutableMap<UUID, Long> = HashMap()

    override val iconColor: Color
        get() = Color.AQUA

    override val textColor: ChatColor
        get() = ChatColor.GREEN

    override val menuDisplay: Material
        get() = Material.DIAMOND_PICKAXE

    override fun getCost(level: Int): Long {
        return (readCost() + (level - 1) * 2500).toLong()
    }

    /**
     * Sends the feedback message to the player.
     */
    override fun onSellAll(player: Player, enchantedItem: ItemStack?, level: Int, event: PlayerSellToShopEvent) {
        if (event.player.hasMetadata("PENDING_EXPORT_FEEDBACK")) {
            event.player.removeMetadata("PENDING_EXPORT_FEEDBACK", PrisonAIO.instance)
            sendMessage(event.player, "You have exported your inventory for a profit of ${ChatColor.AQUA}$${ChatColor.GREEN}${ChatColor.BOLD}${event.getSellCost()}${ChatColor.GRAY}!")
        }
    }

    /**
     * Forces the player to perform the command /sellall when they right-click their pickaxe.
     */
    override fun onInteract(event: PlayerInteractEvent, enchantedItem: ItemStack, level: Int) {
        val isRightClick = event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK
        val miningRegion = RegionsModule.findRegion(event.player.location)
        if (isRightClick && miningRegion.supportsAbilityEnchants()) {
            val cooldownMap = readLevelToCooldownMap()

            val cooldown: Int = cooldownMap.getOrElse(level) {
                cooldownMap.entries.maxBy { it.key }!!.value
            }

            if (useCooldown.containsKey(event.player.uniqueId)) {
                val remainingSeconds = ((System.currentTimeMillis() - useCooldown[event.player.uniqueId]!!) / 1000).toInt()
                if (remainingSeconds < cooldown) {
                    event.isCancelled = true
                    sendMessage(event.player, "${ChatColor.RED}You can't use this ability for another ${formatIntoDetailedString(cooldown - remainingSeconds)}.")
                    return
                }
            }

            useCooldown[event.player.uniqueId] = System.currentTimeMillis() + cooldown * 1000

            event.player.setMetadata("PENDING_EXPORT_FEEDBACK", FixedMetadataValue(PrisonAIO.instance, true))
            event.player.performCommand("sellall")
        }
    }

    /**
     * Remove player from the cooldown map when the player quits.
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        useCooldown.remove(event.player.uniqueId)
    }

    private fun readCost(): Long {
        return EnchantsModule.config.getLong("exporter.cost")
    }

    private fun readLevelToCooldownMap(): Map<Int, Int> {
        val section = EnchantsModule.config.getConfigurationSection("exporter.level-to-cooldown")
        return section.getKeys(false).shuffled().map { it.toInt() to section.getInt(it) }.toMap()
    }

}