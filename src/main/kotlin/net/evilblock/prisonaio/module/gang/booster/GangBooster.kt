/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.booster

import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.gang.GangModule
import org.bukkit.Material
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit

class GangBooster(
    val boosterType: BoosterType,
    val purchasedBy: UUID,
    val expiration: Long = System.currentTimeMillis() + boosterType.duration
) {

    fun getRemainingTime(): Long {
        return expiration - System.currentTimeMillis()
    }

    enum class BoosterType(
        val rendered: String,
        val description: String,
        val icon: Material,
        val price: Int,
        val duration: Long
    ) {
        INCREASED_TROPHIES(
            "Increased Trophies",
            "Increases the chance of finding trophies while mining, for your entire gang, for a given amount of time.",
            Material.DIAMOND_PICKAXE,
            100_000,
            TimeUnit.MINUTES.toMillis(30)
        ),
        INCREASED_MINE_CRATES(
            "Increased MineCrates",
            "Increases the chance of finding MineCrates while mining, for your entire gang, for a given amount of time.",
            Material.CHEST,
            25_000,
            TimeUnit.MINUTES.toMillis(30)
        ),
        SALES_MULTIPLIER(
            "5x Shop Multiplier",
            "Grants every member of your gang a ${DECIMAL_FORMAT.format(GangModule.readSalesMultiplierMod())}x Shop Multiplier for a given amount of time.",
            Material.NETHER_STAR,
            50_000,
            TimeUnit.MINUTES.toMillis(60)
        );

        fun getFormattedDuration(): String {
            return TimeUtil.formatIntoDetailedString((duration / 1000.0).toInt())
        }
    }

    companion object {
        private val DECIMAL_FORMAT = DecimalFormat("#.##")
    }

}