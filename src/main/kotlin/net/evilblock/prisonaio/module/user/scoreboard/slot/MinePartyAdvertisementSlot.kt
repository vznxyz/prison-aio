/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.scoreboard.slot

import net.evilblock.prisonaio.module.mine.variant.mineparty.MinePartyHandler
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.scoreboard.ScoreboardSlot
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object MinePartyAdvertisementSlot : ScoreboardSlot() {

    private val RANGES = listOf(
        0..5000,
        10000..15000,
        20000..25000
    )

    override fun priority(): Int {
        return 3
    }

    override fun render(player: Player, user: User): List<String> {
        return arrayListOf<String>().also { lines ->
            lines.add("  ${MinePartyHandler.SIMPLE_NAME}")
            lines.add("  ${ChatColor.GRAY}Type ${ChatColor.AQUA}/mineparty ${ChatColor.GRAY}to join!")
        }
    }

    override fun canRender(player: Player, user: User): Boolean {
        val event = MinePartyHandler.getEvent() ?: return false

        if (!event.active || event.isExpired() || event.isNearbyMine(player)) {
            return false
        }

        val msPassed = System.currentTimeMillis() - event.startedAt
        if (msPassed >= 25_000L) {
            return false
        }

        return RANGES.any { msPassed in it }
    }

}