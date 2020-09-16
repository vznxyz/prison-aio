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
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object RegionBitmaskCommands {

    @Command(
        names = ["region bitmask list"],
        description = "Lists all of the bitmask types",
        permission = "region.bitmask"
    )
    @JvmStatic
    fun list(sender: CommandSender) {
        for (bitmaskType in RegionBitmask.values()) {
            sender.sendMessage("${ChatColor.GOLD}${bitmaskType.displayName} (${bitmaskType.bitmaskValue}): ${ChatColor.YELLOW}${bitmaskType.description}")
        }
    }

    @Command(
        names = ["region bitmask"],
        description = "Displays a region's bitmask information",
        permission = "region.bitmask"
    )
    @JvmStatic
    fun info(sender: CommandSender, @Param(name = "region") region: Region) {
        if (region !is BitmaskRegion) {
            sender.sendMessage("${ChatColor.RED}Bitmask flags cannot be applied to regions that don't support bitmasks.")
            return
        }

        sender.sendMessage("${ChatColor.YELLOW}Bitmask flags of ${ChatColor.GOLD}${region.getRegionName()}${ChatColor.YELLOW}:")

        for (bitmaskType in RegionBitmask.values()) {
            if (!region.hasBitmask(bitmaskType)) {
                continue
            }

            sender.sendMessage("${ChatColor.GOLD}${bitmaskType.displayName} (${bitmaskType.bitmaskValue}): ${ChatColor.YELLOW}${bitmaskType.description}")
        }

        sender.sendMessage("${ChatColor.GOLD}Raw Bitmask: ${ChatColor.YELLOW}${region.getRawBitmask()}")
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

        region.setBitmask(region.getRawBitmask() + bitmask.bitmaskValue)
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

        region.setBitmask(region.getRawBitmask() - bitmask.bitmaskValue)
        RegionHandler.saveData()

        sender.sendMessage("${ChatColor.GREEN}Removed ${bitmask.displayName} bitmask flag from ${region.getRegionName()}.")
    }

}