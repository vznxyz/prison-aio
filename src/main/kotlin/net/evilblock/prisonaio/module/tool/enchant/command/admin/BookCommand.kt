/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.command.admin

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.tool.enchant.Enchant
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object BookCommand {

    @Command(
        names = ["ebook", "enchbook"],
        description = "Give a player an enchanted book",
        permission = "prisonaio.enchants.ebook"
    )
    @JvmStatic
    fun execute(
        sender: CommandSender,
        @Param(name = "player", defaultValue = "self") target: Player,
        @Param(name = "enchant") enchant: Enchant,
        @Param(name = "level") level: Int,
        @Param(name = "add", defaultValue = "true") add: Boolean
    ) {
        if (target.inventory.firstEmpty() == -1) {
            target.enderChest.addItem(enchant.enchantBook(level, add))
        } else {
            target.inventory.addItem(enchant.enchantBook(level, add))
        }

        if (sender is Player) {
            target.sendMessage("${ChatColor.GREEN}You've been given a ${enchant.getCategory().textColor}${enchant.enchant} $level Book ${ChatColor.GREEN}by ${ChatColor.YELLOW}${sender.name}${ChatColor.GREEN}.")
        } else {
            target.sendMessage("${ChatColor.GREEN}You've been given a ${enchant.getCategory().textColor}${enchant.enchant} $level Book${ChatColor.GREEN}.")
        }

        sender.sendMessage("${ChatColor.GREEN}You've given a ${enchant.getCategory().textColor}${enchant.enchant} $level Book ${ChatColor.GREEN}to ${ChatColor.YELLOW}${target.name}.")
    }

}