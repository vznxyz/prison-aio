/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.challenge

import net.evilblock.prisonaio.module.gang.Gang

class GangChallenges(@Transient internal var gang: Gang) {

    private val completed: MutableSet<GangChallenge> = hashSetOf()

    var blocksMined = 0L

}