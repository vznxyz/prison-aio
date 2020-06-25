/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.enchant.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.module.enchant.AbstractEnchant
import net.evilblock.prisonaio.module.enchant.EnchantsManager
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object AbstractEnchantParameterType : ParameterType<AbstractEnchant?> {

    override fun transform(sender: CommandSender, source: String): AbstractEnchant? {
        val enchant = EnchantsManager.getEnchantById(source)
        if (enchant == null) {
            sender.sendMessage("${ChatColor.RED}Couldn't find an enchantment by the name `$source`.")
        }
        return enchant
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return EnchantsManager.getRegisteredEnchants()
            .filter { it.enchant.equals(source, ignoreCase = true) }
            .map { it.enchant }
    }

}