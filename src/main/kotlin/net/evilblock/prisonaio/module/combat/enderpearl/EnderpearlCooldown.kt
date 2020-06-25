/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.combat.enderpearl

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class EnderpearlCooldown(val uuid: UUID) {

    private var expiresAt: Long = System.currentTimeMillis() + COOLDOWN
    private var notified: Boolean = false

    fun reset() {
        expiresAt = System.currentTimeMillis() + COOLDOWN
        notified = false
    }

    fun getRemainingTime(): Long {
        return expiresAt - System.currentTimeMillis()
    }

    fun getRemainingSeconds(): Double {
        return (10.0 * getRemainingTime() / 1000.0).roundToInt() / 10.0
    }

    fun hasExpired(): Boolean {
        return getRemainingTime() <= 0
    }

    fun hasBeenNotified(): Boolean {
        return notified
    }

    fun notify(player: Player) {
        notified = true
        player.sendMessage("${ChatColor.YELLOW}Your enderpearl cooldown has expired!")
    }

    companion object {
        private val COOLDOWN = TimeUnit.SECONDS.toMillis(16)
    }

}