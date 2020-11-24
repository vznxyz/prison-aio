/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.exchange.menu.button

import net.evilblock.cubed.menu.buttons.TexturedHeadButton
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.prisonaio.module.exchange.menu.CollectItemsMenu
import net.evilblock.prisonaio.module.user.User
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class CollectionButton(private val user: User) : TexturedHeadButton() {

    override fun getName(player: Player): String {
        return "${ChatColor.AQUA}${ChatColor.BOLD}Collect Items"
    }

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also {
            it.add("")
            it.addAll(TextSplitter.split(text = "Collect any items that you have purchased, won from a bid, or have been returned to you."))
            it.add("")

            val unclaimedListings = user.grandExchangeData.getUnclaimedListings()
            if (unclaimedListings.isEmpty()) {
                it.addAll(TextSplitter.split(text = "You don't have any items that need collecting."))
            } else {
                it.addAll(TextSplitter.split(text = "You have ${ChatColor.GREEN}${ChatColor.BOLD}${NumberUtils.format(unclaimedListings.size)} ${ChatColor.GRAY}items that need to be collected."))
            }

            it.add("")
            it.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to collect listings")
        }
    }

    override fun getTexture(player: Player): String {
        val unclaimedListings = user.grandExchangeData.getUnclaimedListings()
        return if (unclaimedListings.isNotEmpty()) {
            Constants.IB_INBOX_NEW_MAIL
        } else {
            Constants.IB_INBOX
        }
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (clickType.isLeftClick) {
            CollectItemsMenu(user).openMenu(player)
        }
    }

}