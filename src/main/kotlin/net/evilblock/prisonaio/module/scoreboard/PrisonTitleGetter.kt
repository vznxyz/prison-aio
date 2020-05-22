package net.evilblock.prisonaio.module.scoreboard

import net.evilblock.cubed.scoreboard.TitleGetter
import org.bukkit.entity.Player

object PrisonTitleGetter : TitleGetter {

    override fun getTitle(player: Player): String {
        return "${PrisonScoreboardAnimation.getCurrentDisplay()}  "
    }

}