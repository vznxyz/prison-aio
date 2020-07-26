/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass

import com.google.gson.annotations.JsonAdapter
import net.evilblock.prisonaio.module.battlepass.tier.Tier
import net.evilblock.prisonaio.module.battlepass.tier.TierHandler
import net.evilblock.prisonaio.module.battlepass.challenge.Challenge
import net.evilblock.prisonaio.module.battlepass.daily.DailyChallengeHandler
import net.evilblock.prisonaio.module.battlepass.challenge.serialize.ChallengeListReferenceSerializer
import net.evilblock.prisonaio.module.battlepass.challenge.serialize.TierSetReferenceSerializer
import net.evilblock.prisonaio.module.battlepass.tier.reward.Reward
import net.evilblock.prisonaio.module.user.User
import org.bukkit.entity.Player

class BattlePassProgress(@Transient var user: User) {

    private var premium: Boolean = false
    private var experience: Int = 0

    private var commandsExecuted: MutableSet<String> = hashSetOf()

    @JsonAdapter(ChallengeListReferenceSerializer::class)
    private val completedChallenges: MutableList<Challenge> = arrayListOf()

    @JsonAdapter(TierSetReferenceSerializer::class)
    private val claimedRewardsPremium: MutableSet<Tier> = hashSetOf()

    @JsonAdapter(TierSetReferenceSerializer::class)
    private val claimedRewardsFree: MutableSet<Tier> = hashSetOf()

    fun isPremium(): Boolean {
        return premium
    }

    fun setPremium(premium: Boolean) {
        this.premium = premium
        user.requiresSave = true
    }

    fun setExperience(exp: Int) {
        experience = exp
        user.requiresSave = true
    }

    fun getExperience(): Int {
        return experience
    }

    fun addExperience(exp: Int) {
        experience += exp
        user.requiresSave = true
    }

    fun isTierUnlocked(tier: Tier): Boolean {
        return experience >= tier.requiredExperience
    }

    fun getCurrentTier(): Tier? {
        return TierHandler.getTiers().sortedBy { it.requiredExperience }.firstOrNull() { experience >= it.requiredExperience }
    }

    fun getNextTier(): Tier? {
        return TierHandler.getTiers().sortedBy { it.requiredExperience }.firstOrNull { experience < it.requiredExperience }
    }

    fun hasCompletedChallenge(challenge: Challenge): Boolean {
        return completedChallenges.contains(challenge)
    }

    fun completeChallenge(challenge: Challenge) {
        if (challenge.daily) {
            DailyChallengeHandler.getSession().getProgress(user.uuid).completeChallenge(challenge)
            return
        }

        completedChallenges.add(challenge)
        experience += challenge.rewardXp
        user.requiresSave = true
    }

    fun claimReward(player: Player, tier: Tier, reward: Reward) {
        if (reward.isFreeReward()) {
            claimedRewardsFree.add(tier)
        } else {
            claimedRewardsPremium.add(tier)
        }

        user.requiresSave = true
    }

    fun hasClaimedReward(tier: Tier, reward: Reward): Boolean {
        return if (reward.isFreeReward()) {
            claimedRewardsFree.contains(tier)
        } else {
            claimedRewardsPremium.contains(tier)
        }
    }

    fun hasExecutedCommand(command: String): Boolean {
        return commandsExecuted.contains(command.trim().toLowerCase())
    }

    fun executedCommand(command: String) {
        commandsExecuted.add(command.trim().toLowerCase())
    }

}