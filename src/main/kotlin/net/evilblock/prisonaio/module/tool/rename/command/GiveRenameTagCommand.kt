/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.rename.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.tool.rename.RenameTagUtils
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object GiveRenameTagCommand {

    @Command(
        names = ["rename-tag give"],
        description = "Give a player a rename tag",
        permission = Permissions.PICKAXE_ADMIN
    )
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player") player: Player, @Param(name = "amount") amount: Int) {
        if (amount !in 1..64) {
            sender.sendMessage("${ChatColor.RED}Invalid amount! Must be within 1-64.")
            return
        }

        val itemStack = RenameTagUtils.makeRenameTag(amount)

        val notInserted = player.inventory.addItem(itemStack)
        if (notInserted.isNotEmpty()) {
            for (item in notInserted.values) {
                val notInsertedAgain = player.enderChest.addItem(item)
                if (notInsertedAgain.isNotEmpty()) {
                    for (again in notInsertedAgain.values) {
                        player.location.world.dropItemNaturally(player.location, again)
                    }
                }
            }
        }

        player.updateInventory()

        sender.sendMessage("${ChatColor.GREEN}")
    }

}