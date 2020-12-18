/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.tool.enchant.impl

import net.evilblock.prisonaio.module.tool.enchant.Enchant
import net.evilblock.prisonaio.module.tool.enchant.EnchantCategory
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object Haste : Enchant("haste", "Haste", 5) {

    override fun getCategory(): EnchantCategory {
        return EnchantCategory.VANILLA
    }

    override val menuDisplay: Material
        get() = Material.BLAZE_ROD

    override fun onHold(player: Player, item: ItemStack?, level: Int) {
        player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, Int.MAX_VALUE, level - 1))
    }

    override fun onUnhold(player: Player) {
        player.removePotionEffect(PotionEffectType.FAST_DIGGING)
    }

//    override fun getCost(level: Int): Long {
//        return (5000 + (level - 1) * 1500).toLong()
//    }

}