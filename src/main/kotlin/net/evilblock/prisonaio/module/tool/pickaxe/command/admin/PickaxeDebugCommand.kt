/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.pickaxe.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.prisonaio.module.tool.pickaxe.PickaxeHandler
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

object PickaxeDebugCommand {

    @Command(
        names = ["pickaxe debug"],
        description = "Prints debug info about a pickaxe",
        permission = "op",
        async = true
    )
    @JvmStatic
    fun execute(player: Player) {
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

        player.sendMessage("")
        player.sendMessage("${ChatColor.GREEN}Debug info of Pickaxe #${ChatColor.GRAY}${pickaxeData.uuid}")
        player.sendMessage("Enchants: ${pickaxeData.enchants.entries.joinToString { "${it.key.enchant} ${it.value}" }}")
        player.sendMessage("Prestige: ${pickaxeData.prestige}")
        player.sendMessage("Blocks Mined: ${pickaxeData.blocksMined}")
        player.sendMessage("")
    }

}