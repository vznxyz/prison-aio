/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.system.sentient.guard.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.flag.Flag
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.cubed.util.hook.WorldEditUtils
import net.evilblock.prisonaio.module.system.sentient.SentientHandler
import net.evilblock.prisonaio.module.system.sentient.guard.entity.PrisonGuard
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object WalkRegionSetCommand {

    @Command(
        names = ["npc walk-region set"],
        description = "Set an NPC's walk region",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Flag(value = ["u", "unset"], description = "Unset the NPC's walk region") unset: Boolean, @Param(name = "guard") guard: PrisonGuard) {
        if (unset) {
            guard.walkRegion = null
        } else {
            val selection = WorldEditUtils.getSelection(player)
            if (selection == null) {
                player.sendMessage("${ChatColor.RED}You need to select a region using the WorldEdit wand!")
                return
            }

            guard.walkRegion = WorldEditUtils.toCuboid(selection)
        }

        EntityManager.saveData()
        player.sendMessage("${ChatColor.GREEN}Updated walk region of ${SentientHandler.getPrisonGuardName()}${ChatColor.GREEN}!")
    }

}