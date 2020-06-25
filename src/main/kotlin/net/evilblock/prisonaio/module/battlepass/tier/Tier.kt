/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.battlepass.tier

import net.evilblock.prisonaio.module.battlepass.tier.reward.Reward

class Tier(val number: Int) {

    var freeReward: Reward? = null
    var premiumReward: Reward? = null
    var requiredExperience: Int = 999

}