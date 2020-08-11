/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.gang.challenge

import net.evilblock.prisonaio.module.gang.Gang

abstract class GangChallenge(val id: String, val reward: Int) {

    abstract fun getRenderedName(): String

    abstract fun renderGoal(): List<String>

    open fun isProgressive(): Boolean {
        return false
    }

    open fun renderProgress(gang: Gang): List<String> {
        return emptyList()
    }

    abstract fun meetsCompletionRequirements(gang: Gang): Boolean

}