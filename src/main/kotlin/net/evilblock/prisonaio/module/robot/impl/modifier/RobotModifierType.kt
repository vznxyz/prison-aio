/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.robot.impl.modifier

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.util.Duration
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

enum class RobotModifierType(
    val displayName: String,
    val color: ChatColor,
    val icon: ItemStack,
    val lore: List<String>,
    val durationBased: Boolean
) {

    OFFLINE_COLLECT(
        displayName = "Offline-Collect",
        color = ChatColor.GOLD,
        icon = ItemStack(Material.HOPPER),
        lore = arrayListOf<String>().also { lore ->
            lore.addAll(TextSplitter.split(text = "Keeps a Robot running and earning money/tokens even when its owner is offline."))
            lore.add("")
            lore.add("${ChatColor.GRAY}Duration: ${ChatColor.RED}${ChatColor.BOLD}{duration}")
        },
        durationBased = true
    );

    fun getColoredName(): String {
        return color.toString() + ChatColor.BOLD.toString() + displayName
    }

    fun renderLore(value: Double, duration: Duration?): List<String> {
        return lore.map { line ->
            ChatColor.translateAlternateColorCodes('&', line)
                .replace("{value}", value.toString())
                .let {
                    if (durationBased && duration != null) {
                        it.replace("{duration}", TimeUtil.formatIntoAbbreviatedString((duration.get() / 1000.0).toInt()))
                    } else {
                        it
                    }
                }
        }
    }

    class ModifierParameterType : ParameterType<RobotModifierType> {
        override fun transform(sender: CommandSender, source: String): RobotModifierType? {
            try {
                return valueOf(source.toUpperCase())
            } catch (e: Exception) {
                sender.sendMessage("${ChatColor.RED}Couldn't find a modifier type by that name!")
            }
            return null
        }

        override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
            return values().map { it.name }
        }
    }

}