package net.evilblock.prisonaio.module.battlepass.progress

import com.google.gson.annotations.JsonAdapter
import net.evilblock.prisonaio.module.battlepass.tier.Tier
import net.evilblock.prisonaio.module.battlepass.tier.TierHandler
import net.evilblock.prisonaio.module.battlepass.challenge.Challenge
import net.evilblock.prisonaio.module.battlepass.challenge.serialize.ChallengeListReferenceSerializer
import net.evilblock.prisonaio.module.battlepass.challenge.serialize.RewardListReferenceSerializer
import net.evilblock.prisonaio.module.battlepass.tier.reward.Reward
import net.evilblock.prisonaio.module.user.User
import org.bukkit.entity.Player

class BattlePass(@Transient var user: User) {

    private var premium: Boolean = false

    private var experience: Int = 0

    @JsonAdapter(ChallengeListReferenceSerializer::class)
    private val completedChallenges: MutableList<Challenge> = arrayListOf()

    @JsonAdapter(RewardListReferenceSerializer::class)
    private val unclaimedRewards: MutableList<Reward> = arrayListOf()

    fun isPremium(): Boolean {
        return premium
    }

    fun setPremium(premium: Boolean) {
        this.premium = premium
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

    fun getNextTier(): Tier? {
        return TierHandler.getTiers().sortedBy { it.requiredExperience }.firstOrNull { experience < it.requiredExperience }
    }

    fun hasCompletedChallenge(challenge: Challenge): Boolean {
        return completedChallenges.contains(challenge)
    }

    fun completeChallenge(challenge: Challenge) {
        completedChallenges.add(challenge)
        experience += challenge.rewardXp
        user.requiresSave = true
    }

    fun getUnclaimedRewards(): List<Reward> {
        return unclaimedRewards.toList()
    }

    fun hasClaimedReward(reward: Reward): Boolean {
        return !unclaimedRewards.contains(reward)
    }

    fun addUnclaimedReward(reward: Reward) {
        unclaimedRewards.add(reward)
        user.requiresSave = true
    }

    fun claimReward(player: Player, reward: Reward) {
        unclaimedRewards.remove(reward)
        user.requiresSave = true

        reward.execute(player)
    }

}