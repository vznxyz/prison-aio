/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RemoveEnchantCommand {

    @Command(
        names = ["remove-enchant", "remove-ench"],
        description = "Remove an enchant from a pickaxe",
        permission = "prisonaio.enchants.remove-enchant"
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "enchant") enchant: AbstractEnchant) {
        if (player.inventory.itemInMainHand == null || !player.inventory.itemInHand.type.name.endsWith("_PICKAXE")) {
            player.sendMessage("${EnchantsManager.CHAT_PREFIX}${ChatColor.RED}You must be holding the pickaxe you would like to remove an enchant from.")
            return
        }

        EnchantsManager.removeEnchant(player.inventory.itemInMainHand, enchant)
        player.updateInventory()
        player.sendMessage("${ChatColor.GREEN}Removed enchant!")
    }

}