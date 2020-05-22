package net.evilblock.prisonaio.module.scoreboard

import net.evilblock.cubed.scoreboard.ScoreboardHandler
import net.evilblock.prisonaio.module.PluginModule

object ScoreboardModule : PluginModule() {

    override fun getName(): String {
        return "Scoreboard"
    }

    override fun getConfigFileName(): String {
        return "scoreboard"
    }

    override fun onEnable() {
        ScoreboardHandler.configure(PrisonTitleGetter, PrisonScoreGetter)
        getPlugin().server.scheduler.runTaskTimerAsynchronously(getPlugin(), PrisonScoreboardAnimation, 1L, 1L)
    }

}