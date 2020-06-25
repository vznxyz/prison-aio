/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame

import net.evilblock.prisonaio.module.PluginModule
import net.evilblock.prisonaio.module.minigame.coinflip.CoinFlipHandler
import net.evilblock.prisonaio.module.minigame.coinflip.command.CoinFlipBrowseCommand

object MinigamesModule : PluginModule() {

    override fun getName(): String {
        return "Minigames"
    }

    override fun getConfigFileName(): String {
        return "minigames"
    }

    override fun onEnable() {
        CoinFlipHandler.initialLoad()
    }

    override fun onDisable() {
        CoinFlipHandler.cancelGames()
    }

    override fun getCommands(): List<Class<*>> {
        return listOf(
            CoinFlipBrowseCommand.javaClass
        )
    }

}