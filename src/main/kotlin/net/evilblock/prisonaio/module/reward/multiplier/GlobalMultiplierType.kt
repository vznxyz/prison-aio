/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.reward.multiplier

import net.evilblock.cubed.command.data.parameter.ParameterType
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

enum class GlobalMultiplierType(val displayName: String, val color: ChatColor) {

    SHOP("Shop", ChatColor.GREEN),
    TOKEN("Token", ChatColor.YELLOW),
    BLOCKS_MINED("Blocks Mined", ChatColor.RED);

    fun getFormattedName(): String {
        return color.toString() + ChatColor.BOLD.toString() + displayName
    }

    object MultiplierTypeParameterType : ParameterType<GlobalMultiplierType> {
        override fun transform(sender: CommandSender, source: String): GlobalMultiplierType? {
            val type = try {
                valueOf(source.toUpperCase())
            } catch (e: Exception) {
                null
            }

            if (type == null) {
                sender.sendMessage("${ChatColor.RED}Invalid multiplier type! Try SHOP or TOKEN.")
            }

            return type
        }

        override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
            return arrayListOf<String>().also { completions ->
                for (type in values()) {
                    completions.add(type.name)
                }
            }
        }
    }

}