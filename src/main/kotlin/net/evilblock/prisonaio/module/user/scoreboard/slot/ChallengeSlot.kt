/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.scoreboard.slot

import net.evilblock.prisonaio.module.user.User
import org.bukkit.entity.Player

class ChallengeSlot(duration: Long) : DurationBasedSlot(duration) {

    override fun priority(): Int {
        return 15
    }

    override fun render(player: Player, user: User): List<String> {
        return emptyList()
    }

    override fun canRender(player: Player, user: User): Boolean {
        return false
    }

}