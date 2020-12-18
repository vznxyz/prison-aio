/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator

import com.intellectualcrafters.plot.`object`.Plot
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.generator.build.GeneratorBuildLevel
import net.evilblock.prisonaio.module.generator.impl.core.CoreGenerator
import net.evilblock.prisonaio.module.generator.impl.core.CoreBuildLevel
import net.evilblock.prisonaio.module.generator.impl.key.KeyGenerator
import net.evilblock.prisonaio.module.generator.impl.key.KeyBuildLevel
import net.evilblock.prisonaio.module.generator.impl.money.MoneyGenerator
import net.evilblock.prisonaio.module.generator.impl.money.MoneyBuildLevel
import net.evilblock.prisonaio.module.generator.impl.token.TokenGenerator
import net.evilblock.prisonaio.module.generator.impl.token.TokenBuildLevel
import net.evilblock.prisonaio.module.generator.schematic.rotate.Rotation
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

enum class GeneratorType(
    val displayName: String,
    val color: ChatColor,
    val icon: ItemStack,
    val description: String
) {

    CORE(
        "Core",
        ChatColor.RED,
        ItemStack(Material.BEACON),
        "The Core is the heart of your generators. Level up your Core to unlock new generator levels."
    ),
    MONEY(
        "Money",
        ChatColor.GREEN,
        ItemStack(Material.GOLD_INGOT),
        "Generates bank-notes with a value that increases based on the Generator's level."
    ),
    TOKEN(
        "Token",
        ChatColor.YELLOW,
        ItemStack(Material.MAGMA_CREAM),
        "Generates an amount of tokens that increases based on the Generator's level."
    ),
    KEY(
        "Key",
        ChatColor.GOLD,
        ItemStack(Material.TRIPWIRE_HOOK),
        "Generates random crate keys, with a rarity chance that in creases based on the Generator's level."
    );

    fun getProperName(plural: Boolean = false): String {
        return buildString {
            append(displayName)

            if (this@GeneratorType != CORE) {
                append(" Generator")
            }

            if (plural) {
                append("s")
            }
        }
    }

    fun getColoredName(plural: Boolean = false): String {
        return buildString {
            append(color.toString() + ChatColor.BOLD + displayName)

            if (this@GeneratorType != CORE) {
                append(" Generator")
            }

            if (plural) {
                append("s")
            }
        }
    }

    fun getLevels(): Array<GeneratorBuildLevel> {
        return when (this) {
            CORE -> {
                CoreBuildLevel.LEVELS
            }
            MONEY -> {
                MoneyBuildLevel.LEVELS
            }
            TOKEN -> {
                TokenBuildLevel.LEVELS
            }
            KEY -> {
                KeyBuildLevel.LEVELS
            }
        }
    }

    fun getMaxLevel(): Int {
        return getLevels().size
    }

    fun createInstance(plot: Plot, owner: UUID, bounds: Cuboid, rotation: Rotation): Generator {
        return when (this) {
            CORE -> {
                CoreGenerator(plot, owner, bounds, rotation)
            }
            MONEY -> {
                MoneyGenerator(plot, owner, bounds, rotation)
            }
            TOKEN -> {
                TokenGenerator(plot, owner, bounds, rotation)
            }
            KEY -> {
                KeyGenerator(plot, owner, bounds, rotation)
            }
        }
    }

}