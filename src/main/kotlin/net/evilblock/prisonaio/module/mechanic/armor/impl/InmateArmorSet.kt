/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.mechanic.armor.impl

import net.evilblock.cubed.CubedConfig
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import net.evilblock.prisonaio.module.mechanic.armor.AbilityArmorSet
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object InmateArmorSet : AbilityArmorSet(
    "Inmate",
    "${ChatColor.YELLOW}${ChatColor.BOLD}Inmate",
    ItemUtils.applySkullTexture(ItemBuilder.of(Material.SKULL_ITEM).data(3).build(), CubedConfig.getNpcTexture("inmate-armor").textureValue),
    ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_CHESTPLATE), Color.fromRGB(16777215)),
    ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_LEGGINGS), Color.fromRGB(16777215)),
    ItemUtils.colorLeatherArmor(ItemStack(Material.LEATHER_BOOTS), Color.fromRGB(16777215))
) {

    override fun getSetDescription(): String {
        return "While the full set is equipped, you will receive 2x more tokens when mining."
    }

    override fun getShortSetDescription(): String {
        return "Receive 2x more tokens when mining"
    }

}