/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.minigame.coinflip.task

import net.evilblock.prisonaio.module.minigame.coinflip.CoinFlipHandler

object CoinFlipGameTicker : Runnable {

    override fun run() {
        for (game in CoinFlipHandler.getGames()) {
            game.tick()
        }
    }

}