/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.modifier

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.util.Duration
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.cubed.util.nms.NBTUtil
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

enum class GeneratorModifier(
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
        listOf(),
        true
    ),
    MULTIPLIER(
        "Multiplier",
        ChatColor.GREEN,
        ItemStack(Material.EMERALD),
        listOf(),
        true
    ),
    AUTO_COLLECT(
        "Auto-Collect",
        ChatColor.GOLD,
        ItemStack(Material.HOPPER),
        listOf(),
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

    fun toItemStack(value: Double, duration: Duration?): ItemStack {
        val item = ItemBuilder.copyOf(icon)
            .name("$color${ChatColor.BOLD}${displayName.toUpperCase()}")
            .setLore(renderLore(value, duration))
            .build()

        GlowEnchantment.addGlow(item)

        val nmsCopy = ItemUtils.getNmsCopy(item)
        val tag = NBTUtil.getOrCreateTag(nmsCopy)

        NBTUtil.setString(tag, "ModifierType", name)
        NBTUtil.setDouble(tag, "ModifierValue", value)

        ItemUtils.preserveItemNBT(nmsCopy, tag, "ModifierType")
        ItemUtils.preserveItemNBT(nmsCopy, tag, "ModifierValue")

        if (durationBased && duration != null) {
            NBTUtil.setLong(tag, "ModifierDuration", duration.get())
            ItemUtils.preserveItemNBT(nmsCopy, tag, "ModifierDuration")
        }

        NBTUtil.setTag(nmsCopy, tag)

        return ItemUtils.getBukkitCopy(nmsCopy)
    }

    class ModifierParameterType : ParameterType<GeneratorModifier> {
        override fun transform(sender: CommandSender, source: String): GeneratorModifier? {
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