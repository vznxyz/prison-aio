/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.activity.type

import net.evilblock.prisonaio.module.user.activity.Activity
import java.time.Instant
import java.util.*

data class CompletedAchievementActivity(
    /**
     * The ID of this achievement
     */
    val achievementId: String,
    /**
     * The date when the user completed this achievement
     */
    private val dateCompleted: Date = Date.from(Instant.now())
) : Activity {

    override fun getActivityText(): String {
        return "Completed achievement $achievementId"
    }

    override fun getDateCompleted(): Date {
        return dateCompleted
    }

}