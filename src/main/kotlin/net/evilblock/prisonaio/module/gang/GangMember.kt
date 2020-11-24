/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang

import net.evilblock.cubed.Cubed
import org.bukkit.ChatColor
import java.util.*

class GangMember(
    val uuid: UUID,
    var invitedBy: UUID? = null,
    var invitedAt: Long? = null
) {

    var role: Role = Role.MEMBER
    val joinedAt: Long = System.currentTimeMillis()
    var lastPlayed: Long = System.currentTimeMillis()

    var trophiesCollected: Long = 0L

    fun getUsername(): String {
        return Cubed.instance.uuidCache.name(uuid)
    }

    enum class Role(val rendered: String, val color: String) {
        MEMBER("Member", "${ChatColor.GRAY}"),
        CAPTAIN("Captain", "${ChatColor.DARK_PURPLE}"),
        CO_LEADER("Co-Leader", "${ChatColor.RED}"),
        LEADER("Leader", "${ChatColor.DARK_RED}${ChatColor.BOLD}");

        fun isBelow(role: Role): Boolean {
            return this.ordinal < role.ordinal
        }

        fun isAbove(role: Role): Boolean {
            return this.ordinal > role.ordinal
        }

        fun isAtLeast(role: Role): Boolean {
            return this.ordinal >= role.ordinal
        }
    }

}