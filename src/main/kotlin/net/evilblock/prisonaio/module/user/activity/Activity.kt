/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.activity

import java.util.*

interface Activity {

    fun getActivityText(): String

    fun getDateCompleted(): Date

}