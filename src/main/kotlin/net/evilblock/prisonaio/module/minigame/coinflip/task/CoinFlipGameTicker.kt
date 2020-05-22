package net.evilblock.prisonaio.module.minigame.coinflip.task

import net.evilblock.prisonaio.module.minigame.coinflip.CoinFlipHandler

object CoinFlipGameTicker : Runnable {

    override fun run() {
        for (game in CoinFlipHandler.getGames()) {
            game.tick()
        }
    }

}