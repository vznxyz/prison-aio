/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.scoreboard.slot

import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.user.User
import net.evilblock.prisonaio.module.user.scoreboard.ScoreboardSlot
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object TeleportSlot : ScoreboardSlot() {

    override fun priority(): Int {
        return 15
    }

    override fun canRender(player: Player, user: User): Boolean {
        return user.pendingTeleport != null && !user.pendingTeleport!!.isExpired()
    }

    override fun render(player: Player, user: User): List<String> {
        return arrayListOf<String>().also { desc ->
            val teleport = user.pendingTeleport!!

            desc.add("  ${ChatColor.YELLOW}${ChatColor.BOLD}Teleporting...")
            desc.add("  ${teleport.name} ${ChatColor.GRAY}(${TimeUtil.formatIntoMMSS(teleport.getRemainingSeconds())})")
        }
    }

}