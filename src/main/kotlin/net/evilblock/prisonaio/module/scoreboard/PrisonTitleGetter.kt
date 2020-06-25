/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.scoreboard

import net.evilblock.cubed.scoreboard.TitleGetter
import org.bukkit.entity.Player

object PrisonTitleGetter : TitleGetter {

    override fun getTitle(player: Player): String {
        return "${PrisonScoreboardAnimation.getCurrentDisplay()}  "
    }

}