/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.scoreboard.animation

object LinkAnimation : Runnable {

    private val LINKS = listOf(
        "      play.minejunkie.com",
        "     store.minejunkie.com",
        "    discord.gg/minejunkie"
    )

    private var index: Int = 0
    private var lastChange: Long = System.currentTimeMillis()

    override fun run() {
        if (System.currentTimeMillis() - lastChange >= 3_000L) {
            lastChange = System.currentTimeMillis()

            if (index + 1 >= LINKS.size) {
                index = 0
            } else {
                index++
            }
        }
    }

    @JvmStatic
    fun getCurrentLink(): String {
        return LINKS[index]
    }

}