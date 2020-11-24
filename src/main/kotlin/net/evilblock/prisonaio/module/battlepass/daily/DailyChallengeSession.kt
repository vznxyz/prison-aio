/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.daily

import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.util.TimeUtil
import net.evilblock.prisonaio.module.battlepass.challenge.Challenge
import net.evilblock.prisonaio.module.battlepass.challenge.ChallengeHandler
import net.evilblock.prisonaio.module.battlepass.challenge.serialize.ChallengeListReferenceSerializer
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.NoSuchElementException
import kotlin.math.ceil

class DailyChallengeSession(val uuid: UUID = UUID.randomUUID()) {

    private val expiresAt: Long = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1L)

    @JsonAdapter(ChallengeListReferenceSerializer::class)
    private val challenges: MutableList<Challenge> = generateChallenges()

    private val progress: MutableMap<UUID, DailyChallengesProgress> = hashMapOf()

    fun getChallenges(): List<Challenge> {
        return challenges
    }

    private fun generateChallenges(): MutableList<Challenge> {
        val filtered = ChallengeHandler.getChallenges().filter { it.daily }.toMutableList()
        val generated = arrayListOf<Challenge>()

        for (i in 0..filtered.size.coerceAtMost(8)) {
            try {
                val challenge = filtered.random()
                filtered.remove(challenge)
                generated.add(challenge)
            } catch (e: NoSuchElementException) {
                break
            }
        }

        return generated
    }

    fun getProgress(uuid: UUID): DailyChallengesProgress {
        if (!progress.containsKey(uuid)) {
            progress[uuid] = DailyChallengesProgress(uuid)
        }
        return progress[uuid]!!
    }

    fun hasExpired(): Boolean {
        return System.currentTimeMillis() >= expiresAt
    }
    
    fun getTimeRemaining(): String {
        val remainingSeconds = ((expiresAt - System.currentTimeMillis()) / 1000.0).toInt()
        return TimeUtil.formatIntoDetailedString(remainingSeconds)
    }

    fun clear() {
        challenges.clear()
        progress.clear()
    }

}