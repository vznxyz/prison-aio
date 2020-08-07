/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import net.evilblock.prisonaio.module.enchant.menu.SalvagePickaxeMenu
import net.evilblock.prisonaio.module.enchant.pickaxe.PickaxeHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object SalvageCommand {

    @Command(
        names = ["salvage"],
        description = "Salvage your pickaxe"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (player.inventory.itemInMainHand == null || !player.inventory.itemInHand.type.name.endsWith("_PICKAXE")) {
            player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}You must be holding the pickaxe you would like to enchant.")
            return
        }

        EnchantsManager.handleItemSwitch(player, player.inventory.itemInMainHand, null)

        val pickaxeData = PickaxeHandler.getPickaxeData(player.inventory.itemInMainHand) ?: return
        SalvagePickaxeMenu(player.inventory.itemInMainHand, pickaxeData).openMenu(player)
    }

}