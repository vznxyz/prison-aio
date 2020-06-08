package net.evilblock.prisonaio.module.battlepass.tier

import net.evilblock.prisonaio.module.battlepass.tier.reward.Reward

class Tier(val number: Int) {

    var freeReward: Reward? = null
    var premiumReward: Reward? = null
    var requiredExperience: Int = 999

}