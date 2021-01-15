/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.scoreboard.animation

import net.evilblock.prisonaio.module.user.scoreboard.ScoreboardHandler

object TitleAnimation : Runnable {

    private var stage: Int = 0
    private var lastStage: Long = System.currentTimeMillis()

    override fun run() {
        if (!ScoreboardHandler.isTitleAnimated()) {
            return
        }

        val stageAt = ScoreboardHandler.getTitleAnimationFrames()[stage]

        if (System.currentTimeMillis() - lastStage >= stageAt.delay) {
            lastStage = System.currentTimeMillis()

            if (stage + 1 >= ScoreboardHandler.getTitleAnimationFrames().size) {
                stage = 0
            } else {
                stage++
            }
        }
    }

    @JvmStatic
    fun getCurrentTitle(): String {
        return ScoreboardHandler.getTitleAnimationFrames()[stage].text
    }

}