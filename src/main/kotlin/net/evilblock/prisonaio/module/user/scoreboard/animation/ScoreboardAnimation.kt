/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.user.scoreboard.animation

import org.bukkit.ChatColor

object ScoreboardAnimation : Runnable {

    private var flicker: Boolean = false
    private var lastFlicker: Long = System.currentTimeMillis()

    private var stage: Int = 0
    private var lastStage: Long = System.currentTimeMillis()

//    private var stages: List<Pair<String, Long>> = listOf(
//        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.WHITE}${ChatColor.BOLD}JUNKIE", 150L),
//        Pair("${ChatColor.DARK_GREEN}${ChatColor.BOLD}M${ChatColor.RED}${ChatColor.BOLD}INE${ChatColor.WHITE}${ChatColor.BOLD}JUNKIE", 150L),
//        Pair("${ChatColor.RED}${ChatColor.BOLD}M${ChatColor.DARK_GREEN}${ChatColor.BOLD}I${ChatColor.RED}${ChatColor.BOLD}NE${ChatColor.WHITE}${ChatColor.BOLD}JUNKIE", 150L),
//        Pair("${ChatColor.RED}${ChatColor.BOLD}MI${ChatColor.DARK_GREEN}${ChatColor.BOLD}N${ChatColor.RED}${ChatColor.BOLD}E${ChatColor.WHITE}${ChatColor.BOLD}JUNKIE", 150L),
//        Pair("${ChatColor.RED}${ChatColor.BOLD}MIN${ChatColor.DARK_GREEN}${ChatColor.BOLD}E${ChatColor.WHITE}${ChatColor.BOLD}JUNKIE", 150L),
//        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.DARK_GREEN}${ChatColor.BOLD}J${ChatColor.WHITE}${ChatColor.BOLD}UNKIE", 150L),
//        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.WHITE}${ChatColor.BOLD}J${ChatColor.DARK_GREEN}${ChatColor.BOLD}U${ChatColor.WHITE}${ChatColor.BOLD}NKIE", 150L),
//        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.WHITE}${ChatColor.BOLD}JU${ChatColor.DARK_GREEN}${ChatColor.BOLD}N${ChatColor.WHITE}${ChatColor.BOLD}KIE", 150L),
//        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.WHITE}${ChatColor.BOLD}JUN${ChatColor.DARK_GREEN}${ChatColor.BOLD}K${ChatColor.WHITE}${ChatColor.BOLD}IE", 150L),
//        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.WHITE}${ChatColor.BOLD}JUNK${ChatColor.DARK_GREEN}${ChatColor.BOLD}I${ChatColor.WHITE}${ChatColor.BOLD}E", 150L),
//        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.WHITE}${ChatColor.BOLD}JUNKI${ChatColor.DARK_GREEN}${ChatColor.BOLD}E", 150L),
//        Pair("${ChatColor.WHITE}${ChatColor.BOLD}MINEJUNKIE", 300L),
//        Pair("${ChatColor.DARK_GREEN}${ChatColor.BOLD}MINEJUNKIE", 300L),
//        Pair("${ChatColor.RED}${ChatColor.BOLD}MINEJUNKIE", 300L),
//        Pair("${ChatColor.WHITE}${ChatColor.BOLD}MINEJUNKIE", 300L),
//        Pair("${ChatColor.DARK_GREEN}${ChatColor.BOLD}MINEJUNKIE", 300L),
//        Pair("${ChatColor.RED}${ChatColor.BOLD}MINEJUNKIE", 300L)
//    )

    private var stages: List<Pair<String, Long>> = listOf(
        Pair("${ChatColor.GRAY}${ChatColor.BOLD}${ChatColor.UNDERLINE}MINE${ChatColor.RED}${ChatColor.BOLD}${ChatColor.UNDERLINE}JUNKIE", 500L),
        Pair("${ChatColor.DARK_GREEN}${ChatColor.BOLD}${ChatColor.UNDERLINE}MINE${ChatColor.DARK_RED}${ChatColor.BOLD}${ChatColor.UNDERLINE}JUNKIE", 500L),
        Pair("${ChatColor.GRAY}${ChatColor.BOLD}${ChatColor.UNDERLINE}MINE${ChatColor.RED}${ChatColor.BOLD}${ChatColor.UNDERLINE}JUNKIE", 500L),
        Pair("${ChatColor.DARK_GREEN}${ChatColor.BOLD}${ChatColor.UNDERLINE}MINE${ChatColor.DARK_RED}${ChatColor.BOLD}${ChatColor.UNDERLINE}JUNKIE", 500L)
    )

    override fun run() {
        if (System.currentTimeMillis() - lastFlicker >= 250L) {
            flicker = !flicker
            lastFlicker = System.currentTimeMillis()
        }

        val stageAt = stages[stage]

        if (System.currentTimeMillis() - lastStage >= stageAt.second) {
            lastStage = System.currentTimeMillis()

            if (stage + 1 >= stages.size) {
                stage = 0
            } else {
                stage++
            }
        }
    }

    @JvmStatic
    fun getCurrentTitle(): String {
        return stages[stage].first
    }

    private val FLICKER = ChatColor.translateAlternateColorCodes('&', "&2&m---&c&m---&f&m---&2&m---&c&m---&f&m---&2&m--")
    private val FLICKER_ALT = ChatColor.translateAlternateColorCodes('&', "&c&m---&f&m---&2&m---&c&m---&f&m---&2&m---&c&m--")

    @JvmStatic
    fun getCurrentBorder(): String {
        return if (flicker) {
            FLICKER
        } else {
            FLICKER_ALT
        }
    }

}