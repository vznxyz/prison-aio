/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.backpack.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.mechanic.backpack.Backpack
import net.evilblock.prisonaio.module.mechanic.backpack.BackpackHandler
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object BackpackGiveCommand {

    @Command(
        names = ["backpack give"],
        description = "Gives a player a backpack",
        permission = "backpack.give"
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") player: Player) {
        val backpack = Backpack(UUID.randomUUID().toString().replace("-", "").substring(0, 13))

        BackpackHandler.trackBackpack(backpack)

        player.inventory.addItem(backpack.toBackpackItem())
        player.updateInventory()

        sender.sendMessage("${ChatColor.GREEN}You've given a backpack to ${player.name}!")
    }

}