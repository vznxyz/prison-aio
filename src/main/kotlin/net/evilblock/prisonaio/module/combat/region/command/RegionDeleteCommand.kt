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
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RegionDeleteCommand {

    @Command(
        names = ["combat region delete"],
        description = "Delete a combat region",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "region") region: CombatRegion) {
        RegionsModule.clearBlockCache(region)

        CombatRegionHandler.forgetRegion(region)
        CombatRegionHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Successfully deleted the `${region.id}` combat region.")
    }

}