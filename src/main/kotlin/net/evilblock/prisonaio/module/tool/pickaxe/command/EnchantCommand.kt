/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.pickaxe.command

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.mechanic.MechanicsModule
import net.evilblock.prisonaio.module.tool.enchant.EnchantHandler
import net.evilblock.prisonaio.module.tool.pickaxe.menu.PurchaseEnchantsMenu
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object EnchantCommand {

    @Command(
        names = ["enchant", "enchants", "ench"],
        description = "Enchant your pickaxe"
    )
    @JvmStatic
    fun execute(player: Player) {
        if (player.inventory.itemInMainHand == null || !MechanicsModule.isPickaxe(player.inventory.itemInMainHand)) {
            player.sendMessage("${EnchantHandler.CHAT_PREFIX}${ChatColor.RED}You must be holding the pickaxe you would like to enchant.")
            return
        }

        val pickaxeData = PickaxeHandler.getPickaxeData(player.inventory.itemInMainHand)
        if (pickaxeData == null) {
            player.sendMessage("${EnchantHandler.CHAT_PREFIX}${ChatColor.RED}Your pickaxe isn't registered! Try switching hands!")
        } else {
            PurchaseEnchantsMenu(player.inventory.itemInMainHand, pickaxeData).openMenu(player)
        }
    }

}