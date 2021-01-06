/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.logger

import net.evilblock.cubed.entity.hologram.updating.UpdatingHologramEntity
import net.evilblock.cubed.util.TimeUtil
import org.bukkit.ChatColor

class CombatLoggerHologram(@Transient private val logger: CombatLogger) : UpdatingHologramEntity(text = "", location = logger.calculateHologramLocation()) {

    override fun getNewLines(): List<String> {
        return listOf(
            "${ChatColor.RED}${logger.owner.name} ${logger.getHealthDisplay()}",
            buildString {
                if (logger.isExpired()) {
                    append("${ChatColor.GRAY}(Combat Logger)")
                } else {
                    append("${ChatColor.GRAY}(Combat Logger - ${TimeUtil.formatIntoMMSS(logger.getRemainingSeconds())})")
                }
            }
        )
    }

    override fun getTickInterval(): Long {
        return 1000L
    }

}