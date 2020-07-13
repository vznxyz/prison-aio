/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.scoreboard

import net.evilblock.cubed.plugin.PluginFramework
import net.evilblock.cubed.plugin.PluginModule
import net.evilblock.cubed.scoreboard.ScoreboardHandler
import net.evilblock.prisonaio.PrisonAIO

object ScoreboardModule : PluginModule() {

    override fun getName(): String {
        return "Scoreboard"
    }

    override fun getConfigFileName(): String {
        return "scoreboard"
    }

    override fun getPluginFramework(): PluginFramework {
        return PrisonAIO.instance
    }

    override fun onEnable() {
        ScoreboardHandler.configure(PrisonTitleGetter, PrisonScoreGetter)
        getPluginFramework().server.scheduler.runTaskTimerAsynchronously(getPluginFramework(), PrisonScoreboardAnimation, 1L, 1L)
    }

}