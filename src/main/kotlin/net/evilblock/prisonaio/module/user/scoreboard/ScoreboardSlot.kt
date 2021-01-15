/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.scoreboard

import net.evilblock.prisonaio.module.user.User
import org.bukkit.entity.Player

abstract class ScoreboardSlot {

    abstract fun priority(): Int

    abstract fun render(player: Player, user: User): List<String>

    abstract fun canRender(player: Player, user: User): Boolean

}