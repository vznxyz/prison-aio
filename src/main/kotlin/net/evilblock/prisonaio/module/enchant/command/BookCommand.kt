package net.evilblock.prisonaio.module.enchant.command

import net.evilblock.cubed.command.Command
import net.evilblock.cubed.command.data.parameter.Param
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object BookCommand {

    @Command(names = ["ebook", "enchbook"], description = "Give a player an enchanted book", permission = "op")
    @JvmStatic
    fun execute(sender: CommandSender, @Param(name = "player", defaultValue = "self") target: Player, @Param(name = "enchant") enchant: AbstractEnchant, @Param(name = "level") level: Int) {
        if (target.inventory.firstEmpty() == -1) {
            target.enderChest.addItem(enchant.enchantBook(level))
        } else {
            target.inventory.addItem(enchant.enchantBook(level))
        }

        if (sender is Player) {
            target.sendMessage("${ChatColor.GREEN}You've been given a ${enchant.textColor}${enchant.enchant} $level Book ${ChatColor.GREEN}by ${ChatColor.YELLOW}${sender.name}${ChatColor.GREEN}.")
        } else {
            target.sendMessage("${ChatColor.GREEN}You've been given a ${enchant.textColor}${enchant.enchant} $level Book${ChatColor.GREEN}.")
        }

        sender.sendMessage("${ChatColor.GREEN}You've given a ${enchant.textColor}${enchant.enchant} $level Book ${ChatColor.GREEN}to ${ChatColor.YELLOW}${target.name}.")
    }

}