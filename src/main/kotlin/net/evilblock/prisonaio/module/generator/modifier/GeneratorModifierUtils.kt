/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.modifier

import net.evilblock.cubed.util.Duration
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.cubed.util.nms.NBTUtil
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object GeneratorModifierUtils {

    @JvmStatic
    fun makeModifierItemStack(type: GeneratorModifierType, amount: Int, value: Double, duration: Duration?): ItemStack {
        val item = ItemBuilder.copyOf(type.icon)
            .amount(amount)
            .name("${type.color}${ChatColor.BOLD}Generator ${type.displayName.toUpperCase()}")
            .setLore(type.renderLore(value, duration))
            .addFlags(
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_ATTRIBUTES
            )
            .build()

        GlowEnchantment.addGlow(item)

        val nmsCopy = ItemUtils.getNmsCopy(item)
        val tag = NBTUtil.getOrCreateTag(nmsCopy)

        NBTUtil.setString(tag, "GeneratorModifierType", type.name)
        NBTUtil.setDouble(tag, "GeneratorModifierValue", value)

        ItemUtils.preserveItemNBT(nmsCopy, tag, "GeneratorModifierType")
        ItemUtils.preserveItemNBT(nmsCopy, tag, "GeneratorModifierValue")

        if (type.durationBased) {
            if (duration != null) {
                NBTUtil.setLong(tag, "GeneratorModifierDuration", duration.get())
                ItemUtils.preserveItemNBT(nmsCopy, tag, "GeneratorModifierDuration")
            } else {
                throw IllegalStateException("Modifier $type.name requires a duration")
            }
        }

        NBTUtil.setTag(nmsCopy, tag)

        return ItemUtils.getBukkitCopy(nmsCopy)
    }

    @JvmStatic
    fun extractModifierFromItemStack(itemStack: ItemStack): GeneratorModifier? {
        try {
            val nmsCopy = ItemUtils.getNmsCopy(itemStack)
            val tag = NBTUtil.getOrCreateTag(nmsCopy)

            if (NBTUtil.hasKey(tag, "GeneratorModifierType") && NBTUtil.hasKey(tag, "GeneratorModifierValue")) {
                val type = GeneratorModifierType.valueOf(NBTUtil.getString(tag, "GeneratorModifierType"))
                val value = NBTUtil.getDouble(tag, "GeneratorModifierValue")

                val duration = if (NBTUtil.hasKey(tag, "GeneratorModifierDuration")) {
                    Duration(NBTUtil.getLong(tag, "GeneratorModifierDuration"))
                } else {
                    null
                }

                return GeneratorModifier(type, value, duration)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

}