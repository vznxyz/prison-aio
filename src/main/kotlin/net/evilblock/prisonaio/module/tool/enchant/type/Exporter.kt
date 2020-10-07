/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.type

import net.evilblock.prisonaio.PrisonAIO
import net.evilblock.prisonaio.module.shop.event.PlayerSellToShopEvent
import net.evilblock.prisonaio.util.Formats
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue

object Exporter : AbilityEnchant(id = "exporter", enchant = "Exporter", maxLevel = 3) {

    override val iconColor: Color
        get() = Color.YELLOW

    override val textColor: ChatColor
        get() = ChatColor.YELLOW

    override val menuDisplay: Material
        get() = Material.DIAMOND_PICKAXE

//    override fun getCost(level: Int): Long {
//        return (readCost() + (level - 1) * 2500)
//    }

    /**
     * Sends the feedback message to the player.
     */
    override fun onSellAll(player: Player, enchantedItem: ItemStack?, level: Int, event: PlayerSellToShopEvent) {
        if (event.player.hasMetadata("PENDING_EXPORT_FEEDBACK")) {
            event.player.removeMetadata("PENDING_EXPORT_FEEDBACK", PrisonAIO.instance)
            sendMessage(event.player, "You have exported your inventory for a profit of ${Formats.formatMoney(event.getCost().toDouble())}${ChatColor.GRAY}!")
        }
    }

    /**
     * Forces the player to perform the command /sellall when they right-click their pickaxe.
     */
    override fun onInteract(event: PlayerInteractEvent, enchantedItem: ItemStack, level: Int) {
        super.onInteract(event, enchantedItem, level)

        if (event.isCancelled) {
            return
        }

        if (!isOnGlobalCooldown(event.player)) {
            return
        }

        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            event.isCancelled = true

            event.player.setMetadata("PENDING_EXPORT_FEEDBACK", FixedMetadataValue(PrisonAIO.instance, true))
            event.player.performCommand("sellall")
        }
    }

}