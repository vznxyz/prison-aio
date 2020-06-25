/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.selection

import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.PrisonAIO
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

object RegionSelection {

    val SELECTION_ITEM = ItemBuilder
        .of(Material.GOLD_AXE)
        .name("${ChatColor.AQUA}${ChatColor.BOLD}Region Selection")
        .setLore(listOf(
            "",
            "${ChatColor.GRAY}Left-click to update selection point 1.",
            "${ChatColor.GRAY}Right-click to update selection point 2."
        ))
        .build()

    val FINISH_SELECTION = "${ChatColor.RED}You must finish your selection before doing that."
    val UPDATED_SELECTION = "${ChatColor.GREEN}Updated selection point ${ChatColor.AQUA}%d ${ChatColor.GREEN}to ${ChatColor.LIGHT_PURPLE}%d${ChatColor.GREEN}, ${ChatColor.LIGHT_PURPLE}%d${ChatColor.GREEN}, ${ChatColor.LIGHT_PURPLE}%d ${ChatColor.GRAY}(${ChatColor.YELLOW}%d blocks${ChatColor.GRAY})"

    fun getSelection(player: Player): Cuboid? {
        val point1 = getSelectionPoint(player, 1)
        val point2 = getSelectionPoint(player, 2)
        if (point1 != null && point2 != null) {
            return Cuboid(point1, point2)
        }
        return null
    }

    fun hasSelection(player: Player): Boolean {
        return getSelectionPoint(player, 1) != null && getSelectionPoint(player, 2) != null
    }

    private fun getSelectionPoint(player: Player, point: Int): Location? {
        val metadataKey = "REG_SEL_PT_$point"
        if (player.hasMetadata(metadataKey)) {
            return player.getMetadata(metadataKey)[0].value() as Location
        }
        return null
    }

    fun setSelectionPoint(player: Player, point: Int, location: Location) {
        val metadataKey = "REG_SEL_PT_$point"
        player.setMetadata(metadataKey, FixedMetadataValue(PrisonAIO.instance, location))
    }

}