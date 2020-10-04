/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.pickaxe.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

object PickaxeSetPrestigeCommand {

    @Command(
        names = ["pickaxe set-prestige"],
        description = "Set the prestige of a pickaxe",
        permission = "prisonaio.pickaxe.set-prestige",
        async = true
    )
    @JvmStatic
    fun execute(player: Player, @Param(name = "prestige") prestige: Int) {
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

        pickaxeData.prestige = prestige
        pickaxeData.applyMeta(itemInHand)

        player.sendMessage("${ChatColor.GREEN}Updated pickaxe's prestige to $prestige!")
    }

}