/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.perk

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.prisonaio.util.Permissions
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

enum class Perk(
    val displayName: String,
    val icon: ItemStack,
    val permission: String? = null
) {

    AUTO_SELL("Auto-Sell", ItemStack(Material.EMERALD), Permissions.PERK_AUTO_SELL),
    AUTO_SMELT("Auto-Smelt", ItemStack(Material.IRON_INGOT)),
    SALES_BOOST("Sales Boost", ItemStack(Material.GOLD_NUGGET)),
    FLY("Fly", ItemStack(Material.FEATHER), Permissions.PERK_FLY);

    object PerkParameterType : ParameterType<Perk> {
        override fun transform(sender: CommandSender, source: String): Perk? {
            return try {
                valueOf(source)
            } catch (e: Exception) {
                sender.sendMessage("${ChatColor.RED}Can't find perk by the name of `$source`.")
                sender.sendMessage("${ChatColor.RED}Available perks: ${values().joinToString { it.name }}")
                null
            }
        }

        override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
            return arrayListOf<String>().also { completed ->
                for (perk in values()) {
                    if (perk.name.startsWith(source, ignoreCase = true)) {
                        completed.add(perk.name.toLowerCase())
                    }
                }
            }
        }
    }

}