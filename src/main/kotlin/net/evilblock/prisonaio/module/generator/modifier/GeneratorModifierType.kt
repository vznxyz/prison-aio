/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.modifier

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.util.Duration
import net.evilblock.cubed.util.TextSplitter
import net.evilblock.cubed.util.TimeUtil
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

enum class GeneratorModifierType(
    val displayName: String,
    val color: ChatColor,
    val icon: ItemStack,
    val lore: List<String>,
    val durationBased: Boolean
) {

    SPEED(
        "Speed",
        ChatColor.AQUA,
        ItemStack(Material.FEATHER),
        arrayListOf<String>().also { lore ->
            lore.addAll(TextSplitter.split(text = "Increases the build speed of a generator."))
            lore.add("")
            lore.add("${ChatColor.GRAY}Multiplier: ${ChatColor.GREEN}${ChatColor.BOLD}{value}x")
            lore.add("${ChatColor.GRAY}Duration: ${ChatColor.RED}${ChatColor.BOLD}{duration}")
        },
        true
    ),
    MULTIPLIER(
        "Multiplier",
        ChatColor.GREEN,
        ItemStack(Material.EMERALD),
        arrayListOf<String>().also { lore ->
            lore.addAll(TextSplitter.split(text = "Increases the production speed of a generator."))
            lore.add("")
            lore.add("${ChatColor.GRAY}Multiplier: ${ChatColor.GREEN}${ChatColor.BOLD}{value}x")
            lore.add("${ChatColor.GRAY}Duration: ${ChatColor.RED}${ChatColor.BOLD}{duration}")
        },
        true
    ),
    AUTO_COLLECT(
        "Auto-Collect",
        ChatColor.GOLD,
        ItemStack(Material.HOPPER),
        arrayListOf<String>().also { lore ->
            lore.addAll(TextSplitter.split(text = "Automatically collects items that a Generator produces to a nearby chest. No more wasting time by having full storage!"))
            lore.add("")
            lore.add("${ChatColor.GRAY}Duration: ${ChatColor.RED}${ChatColor.BOLD}{duration}")
        },
        true
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

    class ModifierParameterType : ParameterType<GeneratorModifierType> {
        override fun transform(sender: CommandSender, source: String): GeneratorModifierType? {
            try {
                return valueOf(source)
            } catch (e: Exception) {
                sender.sendMessage("${ChatColor.RED}Couldn't find a gang booster by that name.")
            }
            return null
        }

        override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
            return values().map { it.name }
        }
    }

}