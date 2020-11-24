/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mine.menu.button

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.prisonaio.module.mine.Mine
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class MineTeleportButton(private val mine: Mine) : Button() {

    override fun getName(player: Player): String {
        return "${ChatColor.GREEN}${ChatColor.BOLD}Teleport"
    }

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also { desc ->
            desc.add("")
            desc.addAll(TextSplitter.split(text = "Teleport to this mine's spawn point."))
            desc.add("")
            desc.add("${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to teleport")
        }
    }

    override fun getMaterial(player: Player): Material {
        return Material.ENDER_PEARL
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (mine.spawnPoint == null) {
            player.sendMessage("${ChatColor.RED}Mine ${ChatColor.WHITE}${mine.id}${ChatColor.RED}'s spawn point has not been set.")
        } else {
            player.sendMessage("${ChatColor.GREEN}Teleporting you to ${ChatColor.WHITE}${mine.id}${ChatColor.GREEN}...")
            player.teleport(mine.spawnPoint)
        }
    }

}