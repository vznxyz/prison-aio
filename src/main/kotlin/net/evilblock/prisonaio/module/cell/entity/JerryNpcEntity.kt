/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.cell.entity

import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.prisonaio.module.cell.Cell
import net.evilblock.prisonaio.module.cell.menu.JerryMenu
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player

class JerryNpcEntity(location: Location) : NpcEntity(listOf(""), location) {

    @Transient
    internal lateinit var cell: Cell

    override fun onRightClick(player: Player) {
        var canOpen = false
        if (player.isOp && player.gameMode == GameMode.CREATIVE && RegionBypass.hasBypass(player)) {
            canOpen = true

            if (!RegionBypass.hasReceivedNotification(player)) {
                RegionBypass.sendNotification(player)
            }
        }

        if (!canOpen) {
            if (cell.owner == player.uniqueId) {
                canOpen = true
            }

            if (cell.isMember(player.uniqueId)) {
                canOpen = true
            }
        }

        if (canOpen) {
            JerryMenu(this).openMenu(player)
        } else {
            player.sendMessage("${ChatColor.RED}Jerry The Prison Guard yells: `You know the rules, ${player.name}! No talking to guards!`")
            player.sendMessage("${ChatColor.RED}(You don't have access to Jerry in this cell)")
        }
    }

}