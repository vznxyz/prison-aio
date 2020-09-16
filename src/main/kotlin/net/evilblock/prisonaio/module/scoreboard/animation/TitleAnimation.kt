/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.prisonaio.module.scoreboard.animation

import org.bukkit.ChatColor

object TitleAnimation : Runnable {

    private var stage: Int = 0
    private var lastStage: Long = System.currentTimeMillis()

    private var stages: List<Pair<String, Long>> = listOf(
        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.GRAY}${ChatColor.BOLD}JUNKIE", 200L),
        Pair("${ChatColor.DARK_RED}${ChatColor.BOLD}M${ChatColor.RED}${ChatColor.BOLD}INE${ChatColor.GRAY}${ChatColor.BOLD}JUNKIE", 200L),
        Pair("${ChatColor.RED}${ChatColor.BOLD}M${ChatColor.DARK_RED}${ChatColor.BOLD}I${ChatColor.RED}${ChatColor.BOLD}NE${ChatColor.GRAY}${ChatColor.BOLD}JUNKIE", 200L),
        Pair("${ChatColor.RED}${ChatColor.BOLD}MI${ChatColor.DARK_RED}${ChatColor.BOLD}N${ChatColor.RED}${ChatColor.BOLD}E${ChatColor.GRAY}${ChatColor.BOLD}JUNKIE", 200L),
        Pair("${ChatColor.RED}${ChatColor.BOLD}MIN${ChatColor.DARK_RED}${ChatColor.BOLD}E${ChatColor.GRAY}${ChatColor.BOLD}JUNKIE", 200L),
        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.WHITE}${ChatColor.BOLD}J${ChatColor.GRAY}${ChatColor.BOLD}UNKIE", 200L),
        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.GRAY}${ChatColor.BOLD}J${ChatColor.WHITE}${ChatColor.BOLD}U${ChatColor.GRAY}${ChatColor.BOLD}NKIE", 200L),
        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.GRAY}${ChatColor.BOLD}JU${ChatColor.WHITE}${ChatColor.BOLD}N${ChatColor.GRAY}${ChatColor.BOLD}KIE", 200L),
        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.GRAY}${ChatColor.BOLD}JUN${ChatColor.WHITE}${ChatColor.BOLD}K${ChatColor.GRAY}${ChatColor.BOLD}IE", 200L),
        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.GRAY}${ChatColor.BOLD}JUNK${ChatColor.WHITE}${ChatColor.BOLD}I${ChatColor.GRAY}${ChatColor.BOLD}E", 200L),
        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.GRAY}${ChatColor.BOLD}JUNKI${ChatColor.WHITE}${ChatColor.BOLD}E${ChatColor.GRAY}${ChatColor.BOLD}", 200L),
        Pair("${ChatColor.DARK_RED}${ChatColor.BOLD}MINE${ChatColor.WHITE}${ChatColor.BOLD}JUNKIE${ChatColor.GRAY}${ChatColor.BOLD}", 300L),
        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.GRAY}${ChatColor.BOLD}JUNKIE", 300L),
        Pair("${ChatColor.DARK_RED}${ChatColor.BOLD}MINE${ChatColor.WHITE}${ChatColor.BOLD}JUNKIE${ChatColor.GRAY}${ChatColor.BOLD}", 300L),
        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.GRAY}${ChatColor.BOLD}JUNKIE", 300L),
        Pair("${ChatColor.DARK_RED}${ChatColor.BOLD}MINE${ChatColor.WHITE}${ChatColor.BOLD}JUNKIE${ChatColor.GRAY}${ChatColor.BOLD}", 300L),
        Pair("${ChatColor.RED}${ChatColor.BOLD}MINE${ChatColor.GRAY}${ChatColor.BOLD}JUNKIE", 300L),
        Pair("${ChatColor.DARK_RED}${ChatColor.BOLD}MINE${ChatColor.WHITE}${ChatColor.BOLD}JUNKIE${ChatColor.GRAY}${ChatColor.BOLD}", 300L)
    )

    override fun run() {
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
    fun getCurrentDisplay(): String {
        return stages[stage].first
    }

}