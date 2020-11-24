/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu.button

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.exchange.GrandExchangeHandler
import net.evilblock.prisonaio.module.exchange.menu.UserListingsMenu
import net.evilblock.prisonaio.module.user.UserHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta

class MyListingsButton : Button() {

    override fun getName(player: Player): String {
        return "${ChatColor.AQUA}${ChatColor.BOLD}My Listings"
    }

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also {
            it.add("")
            it.addAll(TextSplitter.split(text = "Shows all of your listing history, which is any listings you've created."))
            it.add("")

            val activeListings = GrandExchangeHandler.getPlayerListings(player.uniqueId).filter { listing -> !listing.isCompleted() }.size
            it.add("${ChatColor.GRAY}Active Listings: ${ChatColor.GREEN}${ChatColor.BOLD}${NumberUtils.format(activeListings)}")

            val completedListings = GrandExchangeHandler.getPlayerListings(player.uniqueId).filter { listing -> listing.isCompleted() }.size
            it.add("${ChatColor.GRAY}Completed Listings: ${ChatColor.GREEN}${ChatColor.BOLD}${NumberUtils.format(completedListings)}")

            it.add("")
            it.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to view your listings")
        }
    }

    override fun getMaterial(player: Player): Material {
        return Material.SKULL_ITEM
    }

    override fun getDamageValue(player: Player): Byte {
        return 3
    }

    override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
        val meta = itemMeta as SkullMeta
        meta.owner = player.name
        return meta
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (clickType.isLeftClick) {
            UserListingsMenu(UserHandler.getUser(player.uniqueId)).openMenu(player)
        }
    }

}