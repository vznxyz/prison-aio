/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.region.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.util.hook.WorldEditUtils
import net.evilblock.prisonaio.module.combat.region.CombatRegion
import net.evilblock.prisonaio.module.combat.region.CombatRegionHandler
import net.evilblock.prisonaio.module.region.RegionsModule
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RegionCreateCommand {

    @Command(
        names = ["combat region create"],
        description = "Create a combat region",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "id") id: String) {
        val selection = WorldEditUtils.getSelection(player)
        if (selection == null) {
            player.sendMessage("${ChatColor.RED}You need to select a region using the WorldEdit wand!")
            return
        }

        if (CombatRegionHandler.getRegionById(id) != null) {
            player.sendMessage("${ChatColor.RED}A combat region by that ID already exists!")
            return
        }

        val region = CombatRegion(id, WorldEditUtils.toCuboid(selection))

        RegionsModule.updateBlockCache(region)

        CombatRegionHandler.trackRegion(region)
        CombatRegionHandler.saveData()

        player.sendMessage("${ChatColor.GREEN}Successfully created the `${region.id}` combat region.")
    }

}