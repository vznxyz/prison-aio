/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.personal.menu.button

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.mine.variant.personal.PrivateMine
import net.evilblock.prisonaio.module.mine.variant.personal.PrivateMineHandler
import net.evilblock.prisonaio.module.mine.variant.personal.PrivateMineConfig
import net.evilblock.prisonaio.module.mine.variant.personal.menu.ManageSettingsMenu
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class PrivateMineButton(private val parent: Menu, private val privateMine: PrivateMine) : Button() {

    override fun getName(player: Player): String {
        val ownerContext = if (privateMine.owner == player.uniqueId) {
            "${ChatColor.GREEN}${ChatColor.BOLD}Your"
        } else {
            "${ChatColor.AQUA}${ChatColor.BOLD}${privateMine.getOwnerName()}'s"
        }

        return "$ownerContext Private Mine"
    }

    override fun getDescription(player: Player): List<String> {
        val playerLimit = PrivateMineConfig.playerLimit
        val resetInterval = TimeUtil.formatIntoDetailedString((PrivateMineConfig.resetInterval / 1000).toInt())
        val salesTax = privateMine.salesTax

        val description = arrayListOf<String>()

        description.add("${ChatColor.GRAY}Players: ${ChatColor.GREEN}${ChatColor.BOLD}${privateMine.getActivePlayers().size}${ChatColor.GRAY}/${playerLimit}")
        description.add("${ChatColor.GRAY}Reset Interval: ${ChatColor.GREEN}${ChatColor.BOLD}$resetInterval")
        description.add("${ChatColor.GRAY}Sales Tax: ${ChatColor.GREEN}${ChatColor.BOLD}$salesTax%")

        if (PrivateMineHandler.getAccessibleMines(player.uniqueId).contains(privateMine)) {
            if (description.last() != "") {
                description.add("")
            }

            description.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to teleport")
        }

        if (privateMine.owner == player.uniqueId) {
            if (description.last() != "") {
                description.add("")
            }

            description.add("${ChatColor.AQUA}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.AQUA}to open settings")
        }

        return description
    }

    override fun getMaterial(player: Player): Material {
        return Material.DIAMOND_PICKAXE
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        player.closeInventory()

        if (clickType.isLeftClick) {
            PrivateMineHandler.attemptJoinMine(privateMine, player)
        }

        if (clickType.isRightClick) {
            if (privateMine.owner == player.uniqueId) {
                ManageSettingsMenu(parent, privateMine).openMenu(player)
            }
        }
    }

}