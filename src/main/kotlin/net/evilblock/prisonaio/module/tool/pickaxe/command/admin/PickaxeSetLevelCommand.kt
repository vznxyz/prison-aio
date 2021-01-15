/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.pickaxe.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.tool.enchant.Enchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantHandler
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

object PickaxeSetLevelCommand {

    @Command(
        names = ["pickaxe set-level"],
        description = "Set the prestige of a pickaxe",
        permission = "prisonaio.pickaxe.set-level",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "enchant") enchant: Enchant, @Param(name = "level") level: Int) {
        val itemInHand = player.inventory.itemInMainHand
        if (itemInHand == null || itemInHand.type == Material.AIR) {
            player.sendMessage("${ChatColor.RED}You must hold the pickaxe in your hand!")
            return
        }

        val pickaxeData = PickaxeHandler.getPickaxeData(itemInHand)
        if (pickaxeData == null) {
            player.sendMessage("${ChatColor.RED}You're not holding a pickaxe in your hand!")
            return
        }

        if (level < 0 || level > enchant.maxLevel) {
            player.sendMessage("${ChatColor.RED}Level must be between 1-${enchant.maxLevel} for that enchant!")
            return
        }

        if (level == 0) {
            pickaxeData.enchants.remove(enchant)
        } else {
            pickaxeData.enchants[enchant] = level
        }

        EnchantHandler.setLevel(pickaxeData, itemInHand, enchant, level, true)

        player.updateInventory()
        player.sendMessage("${ChatColor.GREEN}Updated pickaxe's ${enchant.enchant} level to $level!")
    }

}