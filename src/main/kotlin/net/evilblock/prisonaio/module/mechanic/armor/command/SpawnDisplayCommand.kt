/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.armor.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.cubed.entity.EntityManager
import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorSet
import net.evilblock.prisonaio.module.mechanic.armor.npc.ArmorDisplay
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SpawnDisplayCommand {

    @Command(
        names = ["npc spawn armor-display"],
        description = "Spawns an Armor Display NPC",
        permission = Permissions.SYSTEM_ADMIN,
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "set") set: AbilityArmorSet) {
        val npc = ArmorDisplay(player.location, set)
        npc.initializeData()
        npc.spawn(player)

        EntityManager.trackEntity(npc)
        EntityManager.saveData()

        player.sendMessage("${ChatColor.GREEN}Spawned an ${set.setName} ${ChatColor.GREEN}armor display!")
    }

}