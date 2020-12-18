/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.entity

import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.menu.GangMenu
import net.evilblock.prisonaio.module.region.bypass.RegionBypass
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player

class JerryNpcEntity(location: Location) : NpcEntity(listOf(""), location) {

    @Transient
    internal lateinit var gang: Gang

    override fun onRightClick(player: Player) {
        var canOpen = RegionBypass.hasBypass(player)
        if (canOpen) {
            RegionBypass.attemptNotify(player)
        }

        if (!canOpen) {
            if (gang.leader == player.uniqueId) {
                canOpen = true
            }

            if (gang.isMember(player.uniqueId)) {
                canOpen = true
            }
        }

        if (canOpen) {
            GangMenu(gang).openMenu(player)
        } else {
            player.sendMessage("${ChatColor.RED}Jerry The Prison Guard yells: `You know the rules, ${player.name}! No talking to guards!`")
            player.sendMessage("${ChatColor.RED}(You don't have access to Jerry in this cell)")
        }
    }

}