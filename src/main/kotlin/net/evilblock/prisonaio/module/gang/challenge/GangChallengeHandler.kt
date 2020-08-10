/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.challenge

import net.evilblock.prisonaio.module.gang.Gang
import net.evilblock.prisonaio.module.gang.challenge.impl.AcquirePrestigesGangChallenge
import net.evilblock.prisonaio.module.gang.challenge.impl.AcquireTrophiesGangChallenge
import net.evilblock.prisonaio.module.gang.challenge.impl.BlocksMinedGangChallenge
import net.evilblock.prisonaio.module.gang.challenge.impl.LeaderboardsPlacementGangChallenge

object GangChallengeHandler {

    private val challenges: MutableMap<String, GangChallenge> = hashMapOf()

    fun initialLoad() {
        trackChallenge(BlocksMinedGangChallenge("blocks-mined-500k", 5_000, 500_000))
        trackChallenge(BlocksMinedGangChallenge("blocks-mined-1m", 10_000, 1_000_000))
        trackChallenge(AcquirePrestigesGangChallenge("acquire-prestiges", 5_000, 250))
        trackChallenge(AcquireTrophiesGangChallenge("acquire-prestiges", 20_000, 200_000))
        trackChallenge(LeaderboardsPlacementGangChallenge("leaderboards-placement", 10_000))
    }

    fun getChallenges(): Collection<GangChallenge> {
        return challenges.values
    }

    fun getChallengeById(id: String): GangChallenge? {
        return challenges[id.toLowerCase()]
    }

    fun trackChallenge(challenge: GangChallenge) {
        challenges[challenge.id.toLowerCase()] = challenge
    }

    fun checkCompletions(gang: Gang) {
        for (challenge in challenges.values) {
            if (!gang.challengesData.hasCompleted(challenge) && challenge.meetsCompletionRequirements(gang)) {
                gang.challengesData.completeChallenge(challenge)
            }
        }
    }

}