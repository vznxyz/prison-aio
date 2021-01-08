/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.robot.impl.modifier

import net.evilblock.cubed.util.Duration
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import net.evilblock.cubed.util.nms.NBTUtil
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object RobotModifierUtils {

    @JvmStatic
    fun makeModifierItemStack(type: RobotModifierType, amount: Int, value: Double, duration: Duration?): ItemStack {
        val item = ItemBuilder.copyOf(type.icon)
            .amount(amount)
            .name("${type.color}${ChatColor.BOLD}Robot ${type.displayName.toUpperCase()}")
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

        NBTUtil.setString(tag, "RobotModifierType", type.name)
        NBTUtil.setDouble(tag, "RobotModifierValue", value)

        ItemUtils.preserveItemNBT(nmsCopy, tag, "RobotModifierType")
        ItemUtils.preserveItemNBT(nmsCopy, tag, "RobotModifierValue")

        if (type.durationBased) {
            if (duration != null) {
                NBTUtil.setLong(tag, "RobotModifierDuration", duration.get())
                ItemUtils.preserveItemNBT(nmsCopy, tag, "RobotModifierDuration")
            } else {
                throw IllegalStateException("Modifier $type.name requires a duration")
            }
        }

        NBTUtil.setTag(nmsCopy, tag)

        return ItemUtils.getBukkitCopy(nmsCopy)
    }

    @JvmStatic
    fun extractModifierFromItemStack(itemStack: ItemStack): RobotModifier? {
        try {
            val nmsCopy = ItemUtils.getNmsCopy(itemStack)
            val tag = NBTUtil.getOrCreateTag(nmsCopy)

            if (NBTUtil.hasKey(tag, "RobotModifierType") && NBTUtil.hasKey(tag, "RobotModifierValue")) {
                val type = RobotModifierType.valueOf(NBTUtil.getString(tag, "RobotModifierType"))
                val value = NBTUtil.getDouble(tag, "RobotModifierValue")

                val duration = if (NBTUtil.hasKey(tag, "RobotModifierDuration")) {
                    Duration(NBTUtil.getLong(tag, "RobotModifierDuration"))
                } else {
                    null
                }

                return RobotModifier(type, value, duration)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

}