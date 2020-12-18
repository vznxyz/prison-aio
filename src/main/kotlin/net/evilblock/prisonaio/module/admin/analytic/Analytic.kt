/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.admin.analytic

import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.math.round

enum class Analytic(
    val displayName: String,
    val defaultValue: Any?,
    val icon: ItemStack
) {

    UNIQUE_JOINS("Unique Joins", 0, ItemBuilder.of(Material.NETHER_STAR).build()),
    BLOCKS_MINED("Blocks Mined", 0, ItemBuilder.of(Material.DIAMOND_PICKAXE).build()),
    TIME_PLAYED("Time Played", 0L, ItemBuilder.of(Material.WATCH).build()),
    LOCKSMITH_KEYS_GIVEN("Locksmith Keys Given", 0, ItemBuilder.of(Material.TRIPWIRE_HOOK).build());

    fun <T> getValue(): T {
        return AnalyticHandler.getValue(this)
    }

    fun <T> updateValue(value: T) {
        AnalyticHandler.updateValue(this, value)
    }

    fun getFormattedValue(): String {
        return when (this) {
            UNIQUE_JOINS, BLOCKS_MINED, LOCKSMITH_KEYS_GIVEN -> NumberUtils.format(getValue<Double>())
            TIME_PLAYED -> TimeUtil.formatIntoDetailedString(round(getValue<Long>() / 1000.0).toInt())
        }
    }

}