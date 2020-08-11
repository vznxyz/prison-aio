/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.challenge

import com.google.gson.annotations.JsonAdapter
import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.GangHandler
import net.evilblock.prisonaio.module.gang.challenge.serialize.GangChallengeSetSerializer
import org.bukkit.ChatColor

class GangChallengesData(@Transient internal var gang: Gang) {

    @JsonAdapter(GangChallengeSetSerializer::class)
    private val completed: MutableSet<GangChallenge> = hashSetOf()

    var blocksMined = 0L
    var acquiredPrestiges = 0L
    var placedLeaderboards: Boolean = false

    fun hasCompleted(challenge: GangChallenge): Boolean {
        return completed.contains(challenge)
    }

    fun completeChallenge(challenge: GangChallenge) {
        completed.add(challenge)
        gang.sendMessagesToMembers("${GangHandler.CHAT_PREFIX}The gang has completed the ${ChatColor.GOLD}${challenge.getRenderedName()} ${ChatColor.GRAY}challenge! (${ChatColor.GOLD}${ChatColor.GOLD}${ChatColor.BOLD}${challenge.reward}${ChatColor.GRAY}")
        gang.giveTrophies(challenge.reward)
    }

}