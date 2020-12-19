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
import net.evilblock.prisonaio.module.generator.impl.key.KeyBuildLevel.KeyChance
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import java.util.*

enum class GeneratorType(
    val configSection: String,
    val displayName: String,
    val color: ChatColor,
    val icon: ItemStack,
    val description: String,
    val createInstance: (Int, ConfigurationSection) -> GeneratorBuildLevel
) {

    CORE(
        "core",
        "Core",
        ChatColor.RED,
        ItemStack(Material.BEACON),
        "The Core is the heart of your generators. Level up your Core to unlock new generator levels.",
        { level, section ->
            CoreBuildLevel(
                number = level,
                schematic = section.getString("schematic"),
                upgradeCost = section.getLong("upgrade-cost"),
                buildTime = section.getInt("build-time"),
                maxBuilds = section.getInt("max-builds"),
                maxMoneyLevel = section.getInt("max-money-level"),
                maxTokenLevel = section.getInt("max-token-level"),
                maxKeyLevel = section.getInt("max-key-level")
            )
        }
    ),
    MONEY(
        "money",
        "Money",
        ChatColor.GREEN,
        ItemStack(Material.GOLD_INGOT),
        "Generates bank-notes with a value that increases based on the Generator's level.",
        { level, section ->
            MoneyBuildLevel(
                number = level,
                schematic = section.getString("schematic"),
                upgradeCost = section.getLong("upgrade-cost"),
                buildTime = section.getInt("build-time"),
                maxMoney = section.getDouble("max-money"),
                moneyPerTick = section.getDouble("money-per-tick"),
                moneyPerItem = section.getDouble("money-per-item")
            )
        }
    ),
    TOKEN(
        "token",
        "Token",
        ChatColor.YELLOW,
        ItemStack(Material.MAGMA_CREAM),
        "Generates an amount of tokens that increases based on the Generator's level.",
        { level, section ->
            TokenBuildLevel(
                number = level,
                schematic = section.getString("schematic"),
                upgradeCost = section.getLong("upgrade-cost"),
                buildTime = section.getInt("build-time"),
                maxTokens = section.getDouble("max-tokens"),
                tokensPerTick = section.getDouble("tokens-per-tick"),
                tokensPerItem = section.getDouble("tokens-per-item")
            )
        }
    ),
    KEY(
        "key",
        "Key",
        ChatColor.GOLD,
        ItemStack(Material.TRIPWIRE_HOOK),
        "Generates random crate keys, with a rarity chance that in creases based on the Generator's level.",
        { level, section ->
            val keySection = section.getConfigurationSection("key-chance")

            KeyBuildLevel(
                number = level,
                schematic = section.getString("schematic"),
                upgradeCost = section.getLong("upgrade-cost"),
                tickInterval = section.getLong("tick-interval"),
                buildTime = section.getInt("build-time"),
                maxKeys = section.getInt("max-keys"),
                keys = keySection.getKeys(false).map {
                    KeyChance(
                        key = it,
                        chance = keySection.getDouble(it)
                    )
                }
            )
        }
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
        return GeneratorHandler.getLevels(this)
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