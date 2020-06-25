/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.region.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.combat.region.CombatRegion
import net.evilblock.prisonaio.module.combat.region.CombatRegionHandler
import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.module.region.selection.RegionSelection
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RegionSetRegionCommand {

    @Command(
        names = ["combat region setregion"],
        description = "Updates a combat region's region",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "region") region: CombatRegion) {
        if (!RegionSelection.hasSelection(player)) {
            player.sendMessage("${ChatColor.RED}You need to select a region using the `/region wand`.")
            return
        }

        RegionsModule.clearBlockCache(region)

        region.setCuboid(RegionSelection.getSelection(player)!!)
        CombatRegionHandler.saveData()

        RegionsModule.updateBlockCache(region)

        player.sendMessage("${ChatColor.GREEN}Successfully updated the region of the `${region.id}` combat region.")
    }

}