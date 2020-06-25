/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.challenge.daily

import com.google.gson.annotations.JsonAdapter
import net.evilblock.prisonaio.module.battlepass.challenge.Challenge
import net.evilblock.prisonaio.module.battlepass.challenge.serialize.ChallengeListReferenceSerializer
import net.evilblock.prisonaio.module.mine.Mine
import net.evilblock.prisonaio.module.user.UserHandler
import java.util.*

class DailyChallengesProgress(val uuid: UUID) {

    @JsonAdapter(ChallengeListReferenceSerializer::class)
    private var completedChallenges: MutableList<Challenge> = arrayListOf()

    private var playTime: Long = 0L
    private var blocksMined: Int = 0
    private var blocksMinedAtMine: MutableMap<String, Int> = hashMapOf()
    private var timesPrestiged: Int = 0

    @Synchronized
    fun hasCompletedChallenge(challenge: Challenge): Boolean {
        synchronized(completedChallenges) {
            return completedChallenges.contains(challenge)
        }
    }

    @Synchronized
    fun completeChallenge(challenge: Challenge) {
        synchronized(completedChallenges) {
            completedChallenges.add(challenge)
            UserHandler.getUser(uuid).battlePassData.addExperience(challenge.rewardXp)
        }
    }

    fun addBlocksMined(amount: Int) {
        blocksMined += amount
    }

    fun getBlocksMined(): Int {
        return blocksMined
    }

    fun setBlocksMined(amount: Int) {
        blocksMined = amount
    }

    fun getBlocksMinedAtMine(mine: Mine): Int {
        return blocksMinedAtMine.getOrDefault(mine.id.toLowerCase(), 0)
    }

    fun setBlocksMinedAtMine(mine: Mine, amount: Int) {
        blocksMinedAtMine[mine.id.toLowerCase()] = amount
    }

    fun addBlocksMinedAtMine(mine: Mine, amount: Int) {
        val previous = blocksMinedAtMine.getOrDefault(mine.id.toLowerCase(), 0)
        blocksMinedAtMine[mine.id.toLowerCase()] = previous + amount
    }

    fun getPlayTime(): Long {
        return playTime
    }

    fun addPlayTime(time: Long) {
        playTime += time
    }

    fun getTimesPrestiged(): Int {
        return timesPrestiged
    }

    fun addTimePrestiged() {
        timesPrestiged++
    }

}