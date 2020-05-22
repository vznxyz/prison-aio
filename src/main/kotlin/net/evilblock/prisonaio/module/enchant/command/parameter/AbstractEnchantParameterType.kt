package net.evilblock.prisonaio.module.enchant.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object AbstractEnchantParameterType : ParameterType<AbstractEnchant?> {

    override fun transform(sender: CommandSender, source: String): AbstractEnchant? {
        for (enchant in EnchantsManager.getRegisteredEnchants()) {
            if (enchant.enchant.equals(source, ignoreCase = true)) {
                return enchant
            }
        }

        sender.sendMessage("${ChatColor.RED}Couldn't find an enchantment by the name `$source`.")

        return null
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return EnchantsManager.getRegisteredEnchants()
            .filter { it.enchant.equals(source, ignoreCase = true) }
            .map { it.enchant }
    }

}