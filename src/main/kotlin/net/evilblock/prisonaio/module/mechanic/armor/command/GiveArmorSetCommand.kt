/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.armor.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorSet
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GiveArmorSetCommand {

    @Command(
        names = ["ability-armor give set"],
        description = "Give an armor set to a player",
        permission = Permissions.ABILITY_ARMOR_GIVE
    )
    @JvmStatic
    fun execute(
        sender: CommandSender,
        @Param(name = "player") player: Player,
        @Param(name = "set") set: AbilityArmorSet
    ) {
        player.inventory.addItem(set.getHelmet())
        player.inventory.addItem(set.getChestplate())
        player.inventory.addItem(set.getLeggings())
        player.inventory.addItem(set.getBoots())
        player.updateInventory()

        sender.sendMessage("${ChatColor.GREEN}You gave ${ChatColor.WHITE}${player.name} ${ChatColor.GREEN}the ${set.setName} ${ChatColor.GREEN}set!")
    }

}