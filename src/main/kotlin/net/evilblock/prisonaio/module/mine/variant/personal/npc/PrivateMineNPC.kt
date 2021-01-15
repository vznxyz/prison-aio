/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.variant.personal.npc

import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.prisonaio.module.mine.variant.personal.PrivateMineHandler
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player

class PrivateMineNPC(location: Location) : NpcEntity(lines = listOf("${ChatColor.AQUA}${ChatColor.BOLD}Sell Your Inventory"), location = location) {

    override fun initializeData() {
        super.initializeData()

        updateTexture(PrivateMineHandler.getPrivateMineNPCTextureValue(), PrivateMineHandler.getPrivateMineNPCTextureSignature())
    }

    override fun onRightClick(player: Player) {
        player.performCommand("sellall")
    }

}