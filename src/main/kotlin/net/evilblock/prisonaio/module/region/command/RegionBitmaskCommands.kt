/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.region.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.region.Region
import net.evilblock.prisonaio.module.region.RegionHandler
import net.evilblock.prisonaio.module.region.bitmask.BitmaskRegion
import net.evilblock.prisonaio.module.region.bitmask.RegionBitmask
import net.evilblock.prisonaio.module.region.bitmask.menu.EditRegionBitmaskMenu
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object RegionBitmaskCommands {

    @Command(
        names = ["region bitmask list"],
        description = "Lists all of the bitmask types",
        permission = "region.bitmask"
    )
    @JvmStatic
    fun list(sender: CommandSender) {
        for (bitmaskType in RegionBitmask.values()) {
            sender.sendMessage("${ChatColor.GOLD}${bitmaskType.name} (${bitmaskType.bitmaskValue}): ${ChatColor.YELLOW}${bitmaskType.description}")
        }
    }

    @Command(
        names = ["region bitmask"],
        description = "Displays a region's bitmask information",
        permission = "region.bitmask"
    )
    @JvmStatic
    fun info(player: Player, @Param(name = "region") region: Region) {
        if (region !is BitmaskRegion) {
            player.sendMessage("${ChatColor.RED}Bitmask flags cannot be applied to regions that don't support bitmasks.")
            return
        }

        EditRegionBitmaskMenu(region).openMenu(player)
    }

    @Command(
        names = ["region bitmask add"],
        description = "Adds a bitmask flag to a region",
        permission = "region.bitmask"
    )
    @JvmStatic
    fun add(
        sender: CommandSender,
        @Param(name = "region") region: Region,
        @Param(name = "bitmask") bitmask: RegionBitmask
    ) {
        if (region !is BitmaskRegion) {
            sender.sendMessage("${ChatColor.RED}Bitmask flags cannot be applied to regions that don't support bitmasks.")
            return
        }

        if (region.hasBitmask(bitmask)) {
            sender.sendMessage("${ChatColor.RED}That region doesn't have the ${bitmask.displayName} bitmask flag.")
            return
        }

        region.addBitmask(bitmask)
        RegionHandler.saveData()

        sender.sendMessage("${ChatColor.GREEN}Applied ${bitmask.displayName} bitmask flag to ${region.getRegionName()}.")
    }

    @Command(
        names = ["region bitmask remove"],
        description = "Removes a bitmask flag from a region",
        permission = "region.bitmask"
    )
    @JvmStatic
    fun remove(
        sender: CommandSender,
        @Param(name = "region") region: Region,
        @Param(name = "bitmask") bitmask: RegionBitmask
    ) {
        if (region !is BitmaskRegion) {
            sender.sendMessage("${ChatColor.RED}Bitmask flags cannot be applied to regions that don't support bitmasks.")
            return
        }

        if (!region.hasBitmask(bitmask)) {
            sender.sendMessage("${ChatColor.RED}That region doesn't have the ${bitmask.displayName} bitmask flag.")
            return
        }

        region.removeBitmask(bitmask)
        RegionHandler.saveData()

        sender.sendMessage("${ChatColor.GREEN}Removed ${bitmask.displayName} bitmask flag from ${region.getRegionName()}.")
    }

}