/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.generator.impl.money

import com.intellectualcrafters.plot.`object`.Plot
import net.evilblock.cubed.util.NumberUtils
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import net.evilblock.prisonaio.module.generator.Generator
import net.evilblock.prisonaio.module.generator.GeneratorType
import net.evilblock.prisonaio.module.generator.modifier.GeneratorModifierType
import net.evilblock.prisonaio.module.generator.schematic.rotate.Rotation
import net.evilblock.prisonaio.util.Formats
import net.evilblock.prisonaio.module.mechanic.economy.Currency
import net.evilblock.prisonaio.util.statistic.EarningsHistoryV2
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

class MoneyGenerator(plot: Plot, owner: UUID, bounds: Cuboid, rotation: Rotation) : Generator(UUID.randomUUID(), plot.id, owner, bounds, rotation) {

    private var uncollected: BigDecimal = BigDecimal(0.0)
    private var storedNotes: BigInteger = BigInteger("0")

    private var multiplier = 1.0

    private var uptime: Long = 0L
    private var earnings: EarningsHistoryV2 = EarningsHistoryV2()

    override fun getAbstractType(): Type {
        return MoneyGenerator::class.java
    }

    override fun getGeneratorType(): GeneratorType {
        return GeneratorType.MONEY
    }

    override fun getLevel(): MoneyBuildLevel {
        return super.getLevel() as MoneyBuildLevel
    }

    override fun getNextLevel(): MoneyBuildLevel? {
        return super.getNextLevel() as MoneyBuildLevel?
    }

    override fun tick() {
        super.tick()

        if (!build.finished) {
            return
        }

        tickUptime()
        tickRevenue()
        tickModifiers()
    }

    private fun tickUptime() {
        val timePassed = System.currentTimeMillis() - lastTick
        if (timePassed < 3_000L) {
            uptime += timePassed
        }
    }

    private fun tickRevenue() {
        val level = getLevel()

        val totalStored = (storedNotes.toBigDecimal() * level.moneyPerItem) + uncollected
        if (totalStored < level.maxMoney) {
            val revenue = level.moneyPerTick * BigDecimal(multiplier)

            earnings.addEarnings(revenue)

            if (System.currentTimeMillis() >= earnings.lastAggregation + 60_000L) {
                earnings.aggregate()
            }

            uncollected += revenue

            if (uncollected >= level.moneyPerItem) {
                storedNotes++
                uncollected = BigDecimal(0.0)
            }
        }
    }

    private fun tickModifiers() {
        val level = getLevel()

        val player = Bukkit.getPlayer(owner)
        if (player != null && hasActiveModifier(GeneratorModifierType.AUTO_COLLECT) && storedNotes > BigInteger.ZERO) {
            storedNotes = BigInteger("0")
            Currency.Type.MONEY.give(player.uniqueId, level.moneyPerItem * storedNotes.toBigDecimal())
        }
    }

    override fun getMaxModifiers(): Int {
        return 3
    }

    override fun isModifierCompatible(type: GeneratorModifierType): Boolean {
        return if (build.finished) {
            type == GeneratorModifierType.MULTIPLIER || type == GeneratorModifierType.AUTO_COLLECT
        } else {
            super.isModifierCompatible(type)
        }
    }

    override fun renderInformation(): MutableList<String> {
        return super.renderInformation().also { info ->
            if (build.finished) {
                renderGeneratingInfo(info)
            } else {
                info.add("${ChatColor.GOLD}${ChatColor.BOLD}${ChatColor.UNDERLINE}Revenue History")
                info.add("")
                info.add("${ChatColor.GRAY}No data to display...")
            }
        }
    }

    private fun renderGeneratingInfo(info: MutableList<String>) {
        val level = getLevel()
        val storageAmount = (BigDecimal(storedNotes) * level.moneyPerItem) + uncollected
        val storageFull = storageAmount >= level.maxMoney

        info.add("${ChatColor.GRAY}Multiplier: ${ChatColor.GREEN}${ChatColor.BOLD}${NumberUtils.formatDecimal(multiplier)}x")

        info.add(buildString {
            append("${ChatColor.GRAY}Storage: ")
            append(Formats.formatMoney(storageAmount))
            append("${ChatColor.GRAY}/")
            append("$${NumberUtils.format(level.maxMoney)}")

            if (storageFull) {
                append(" ${ChatColor.RED}${ChatColor.BOLD}FULL")
            }
        })

        info.add("")
        info.add("${ChatColor.GOLD}${ChatColor.BOLD}${ChatColor.UNDERLINE}Revenue History")
        info.add("")
        info.add("${ChatColor.GRAY}1H   ${Formats.formatMoney(earnings.lastHour)}")
        info.add("${ChatColor.GRAY}24H ${Formats.formatMoney(earnings.lastDay)}")
        info.add("${ChatColor.GRAY}7D   ${Formats.formatMoney(earnings.lastWeek)}")
        info.add("${ChatColor.GRAY}ALL ${Formats.formatMoney(earnings.allTime)}")
    }

}